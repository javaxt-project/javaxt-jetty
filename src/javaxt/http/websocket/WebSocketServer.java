package javaxt.http.websocket;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.function.Consumer;


//Core Jetty includes
import javax.servlet.ServletContext;
import javaxt.http.servlet.HttpServlet;
import javaxt.http.servlet.HttpServletRequest;
import javaxt.http.servlet.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.MappedByteBufferPool;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnection;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.DecoratedObjectFactory;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.util.thread.Scheduler;

//WebSocket includes
import org.eclipse.jetty.websocket.api.InvalidWebSocketException;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.api.util.QuoteUtil;
import org.eclipse.jetty.websocket.common.LogicalConnection;
import org.eclipse.jetty.websocket.common.SessionFactory;
import org.eclipse.jetty.websocket.common.WebSocketSession;
import org.eclipse.jetty.websocket.common.WebSocketSessionFactory;
import org.eclipse.jetty.websocket.common.WebSocketSessionListener;
import org.eclipse.jetty.websocket.common.events.EventDriver;
import org.eclipse.jetty.websocket.common.events.EventDriverFactory;
import org.eclipse.jetty.websocket.common.extensions.ExtensionStack;
import org.eclipse.jetty.websocket.common.extensions.WebSocketExtensionFactory;
import org.eclipse.jetty.websocket.common.io.AbstractWebSocketConnection;
import org.eclipse.jetty.websocket.common.scopes.WebSocketContainerScope;
import org.eclipse.jetty.websocket.server.HandshakeRFC6455;
import org.eclipse.jetty.websocket.server.WebSocketHandshake;
import org.eclipse.jetty.websocket.server.WebSocketServerConnection;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;

//******************************************************************************
//**  WebSocketServer
//******************************************************************************
/**
 *   Used to configure a servlet to accept WebSocket connections. This class
 *   is typically invoked in the servlet init() method and called within the
 *   processRequest() method. See WebSocketListener for more information.
 *
 ******************************************************************************/

public class WebSocketServer {


    private static final Logger LOG = Log.getLogger(WebSocketServer.class);

    private WebSocketContainerLifeCycle container;


  //**************************************************************************
  //** Constructor
  //**************************************************************************

    public WebSocketServer(HttpServlet servlet){

      //Get the Jetty handler from the ServletContext (see Server.RequestHandler)
        AbstractHandler handler = (AbstractHandler)
        servlet.getServletContext().getAttribute("org.eclipse.jetty.server.Handler");

        container = new WebSocketContainerLifeCycle(handler.getServer().getThreadPool());

        handler.addBean(container);
    }


  //**************************************************************************
  //** accept
  //**************************************************************************
  /** Returns true if this class can process a given request.
   */
    public boolean accept(HttpServletRequest request){
        return container.isUpgradeRequest(request);
    }


  //**************************************************************************
  //** upgrade
  //**************************************************************************
  /** Used to upgrade a HTTP request to a WebSocket connection
   */
    public void processRequest(WebSocketAdapter wrapper, HttpServletRequest request, HttpServletResponse response){
        try{
            wrapper = container.getObjectFactory().decorate(wrapper);


            javax.servlet.http.HttpServletRequest req = (javax.servlet.http.HttpServletRequest)
                request.getAttribute("javax.servlet.http.HttpServletRequest");
            javax.servlet.http.HttpServletResponse rsp = (javax.servlet.http.HttpServletResponse)
                request.getAttribute("javax.servlet.http.HttpServletResponse");


            ServletUpgradeRequest sockreq = new ServletUpgradeRequest(req);
            ServletUpgradeResponse sockresp = new ServletUpgradeResponse(rsp);
            if (sockresp.isCommitted()){
                return;
            }


          //Pass the WebSocketListener to the webSocketServer
            EventDriver driver = container.getEventDriverFactory().wrap(wrapper);
            HttpConnection connection = (HttpConnection) request.getAttribute("org.eclipse.jetty.server.HttpConnection");
            container.upgrade(connection, sockreq, sockresp, driver);
        }
        catch(Exception e){

        }
    }




  //**************************************************************************
  //** WebSocketContainerLifeCycle
  //**************************************************************************
  /** This class is stripped down version of Jetty's WebSocketServerFactory
   *  class. The constructor is adapted from the WebSocketHandler class.
   */
    private class WebSocketContainerLifeCycle extends ContainerLifeCycle implements WebSocketContainerScope {


        private final Map<Integer, WebSocketHandshake> handshakes = new HashMap<>();
        private final Scheduler scheduler = new ScheduledExecutorScheduler();
        private final List<WebSocketSessionListener> listeners = new CopyOnWriteArrayList<>();
        private final String supportedVersions;
        private final WebSocketPolicy policy;
        private final EventDriverFactory eventDriverFactory;
        private final ByteBufferPool bufferPool;
        private final WebSocketExtensionFactory extensionFactory;
        private final ServletContext context; // can be null when this factory is used from WebSocketHandler
        private final List<SessionFactory> sessionFactories = new ArrayList<>();
        private Executor executor;
        private DecoratedObjectFactory objectFactory;


        private WebSocketContainerLifeCycle(Executor executor){

          //Set class variables
            this.policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
            this.bufferPool = new MappedByteBufferPool();
            this.context = null; //servlet.getServletContext()?
            this.objectFactory = new DecoratedObjectFactory();
            this.executor = executor;

            handshakes.put(HandshakeRFC6455.VERSION, new HandshakeRFC6455());

            addBean(scheduler);
            addBean(bufferPool);



            this.eventDriverFactory = new EventDriverFactory(this);
            this.extensionFactory = new WebSocketExtensionFactory(this);
            this.sessionFactories.add(new WebSocketSessionFactory(this));


            // Create supportedVersions
            List<Integer> versions = new ArrayList<>();
            for (int v : handshakes.keySet())
            {
                versions.add(v);
            }
            Collections.sort(versions, Collections.reverseOrder()); // newest first
            StringBuilder rv = new StringBuilder();
            for (int v : versions)
            {
                if (rv.length() > 0)
                {
                    rv.append(", ");
                }
                rv.append(v);
            }
            supportedVersions = rv.toString();
        }


        @Override
        public void addSessionListener(WebSocketSessionListener listener)
        {
            this.listeners.add(listener);
        }

        @Override
        public void removeSessionListener(WebSocketSessionListener listener)
        {
            this.listeners.remove(listener);
        }

        @Override
        public Collection<WebSocketSessionListener> getSessionListeners()
        {
            return this.listeners;
        }

        private WebSocketSession createSession(URI requestURI, EventDriver websocket, LogicalConnection connection)
        {
            if (websocket == null)
            {
                throw new InvalidWebSocketException("Unable to create Session from null websocket");
            }

            for (SessionFactory impl : sessionFactories)
            {
                if (impl.supports(websocket))
                {
                    try
                    {
                        return impl.createSession(requestURI, websocket, connection);
                    }
                    catch (Throwable e)
                    {
                        throw new InvalidWebSocketException("Unable to create Session", e);
                    }
                }
            }

            throw new InvalidWebSocketException("Unable to create Session: unrecognized internal EventDriver type: " + websocket.getClass().getName());
        }


        @Override
        protected void doStart() throws Exception
        {

            if(this.objectFactory == null && context != null)
            {
                this.objectFactory = (DecoratedObjectFactory) context.getAttribute(DecoratedObjectFactory.ATTR);
                if (this.objectFactory == null)
                {
                    throw new IllegalStateException("Unable to find required ServletContext attribute: " + DecoratedObjectFactory.ATTR);
                }
            }

            if(this.executor == null && context != null)
            {
                ContextHandler contextHandler = ContextHandler.getContextHandler(context);
                this.executor = contextHandler.getServer().getThreadPool();
            }

            Objects.requireNonNull(this.objectFactory, DecoratedObjectFactory.class.getName());
            Objects.requireNonNull(this.executor, Executor.class.getName());

            super.doStart();
        }

        @Override
        public ByteBufferPool getBufferPool()
        {
            return this.bufferPool;
        }


        @Override
        public Executor getExecutor()
        {
            return this.executor;
        }

        @Override
        public DecoratedObjectFactory getObjectFactory()
        {
            return objectFactory;
        }

        protected EventDriverFactory getEventDriverFactory()
        {
            return eventDriverFactory;
        }



        protected Collection<WebSocketSession> getOpenSessions()
        {
            return getBeans(WebSocketSession.class);
        }

        @Override
        public WebSocketPolicy getPolicy()
        {
            return policy;
        }

        @Override
        public SslContextFactory getSslContextFactory()
        {
            /* Not relevant for a Server, as this is defined in the
             * Connector configuration
             */
            return null;
        }

        //@Override
        private boolean isUpgradeRequest(HttpServletRequest request)
        {
            // Tests sorted by least common to most common.

            String upgrade = request.getHeader("Upgrade");
            if (upgrade == null)
            {
                // no "Upgrade: websocket" header present.
                return false;
            }

            if (!"websocket".equalsIgnoreCase(upgrade))
            {
                // Not a websocket upgrade
                return false;
            }

            String connection = request.getHeader("Connection");
            if (connection == null)
            {
                // no "Connection: upgrade" header present.
                return false;
            }

            // Test for "Upgrade" token
            boolean foundUpgradeToken = false;
            Iterator<String> iter = QuoteUtil.splitAt(connection, ",");
            while (iter.hasNext())
            {
                String token = iter.next();
                if ("upgrade".equalsIgnoreCase(token))
                {
                    foundUpgradeToken = true;
                    break;
                }
            }

            if (!foundUpgradeToken)
            {
                return false;
            }

            if (!"GET".equalsIgnoreCase(request.getMethod()))
            {
                // not a "GET" request (not a websocket upgrade)
                return false;
            }

            if (!"HTTP/1.1".equals(request.getProtocol()))
            {
                LOG.debug("Not a 'HTTP/1.1' request (was [" + request.getProtocol() + "])");
                return false;
            }

            return true;
        }

//        @Override
//        public void onSessionOpened(WebSocketSession session)
//        {
//            addManaged(session);
//            notifySessionListeners(listener -> listener.onOpened(session));
//        }
//
//        @Override
//        public void onSessionClosed(WebSocketSession session)
//        {
//            removeBean(session);
//            notifySessionListeners(listener -> listener.onClosed(session));
//        }

        private void notifySessionListeners(Consumer<WebSocketSessionListener> consumer)
        {
            for (WebSocketSessionListener listener : listeners)
            {
                try
                {
                    consumer.accept(listener);
                }
                catch (Throwable x)
                {
                    LOG.info("Exception while invoking listener " + listener, x);
                }
            }
        }


        /**
         * Upgrade the request/response to a WebSocket Connection.
         * <p/>
         * This method will not normally return, but will instead throw a UpgradeConnectionException, to exit HTTP handling and initiate WebSocket handling of the
         * connection.
         *
         * @param http the raw http connection
         * @param request The request to upgrade
         * @param response The response to upgrade
         * @param driver The websocket handler implementation to use
         */
        private boolean upgrade(HttpConnection http, ServletUpgradeRequest request, ServletUpgradeResponse response, EventDriver driver) throws IOException
        {
            if (!"websocket".equalsIgnoreCase(request.getHeader("Upgrade")))
            {
                throw new IllegalStateException("Not a 'WebSocket: Upgrade' request");
            }
            if (!"HTTP/1.1".equals(request.getHttpVersion()))
            {
                throw new IllegalStateException("Not a 'HTTP/1.1' request");
            }

            int version = request.getHeaderInt("Sec-WebSocket-Version");
            if (version < 0)
            {
                // Old pre-RFC version specifications (header not present in RFC-6455)
                version = request.getHeaderInt("Sec-WebSocket-Draft");
            }

            WebSocketHandshake handshaker = handshakes.get(version);
            if (handshaker == null)
            {
                StringBuilder warn = new StringBuilder();
                warn.append("Client ").append(request.getRemoteAddress());
                warn.append(" (:").append(request.getRemotePort());
                warn.append(") User Agent: ");
                String ua = request.getHeader("User-Agent");
                if (ua == null)
                {
                    warn.append("[unset] ");
                }
                else
                {
                    warn.append('"').append(StringUtil.sanitizeXmlString(ua)).append("\" ");
                }
                warn.append("requested WebSocket version [").append(version);
                warn.append("], Jetty supports version");
                if (handshakes.size() > 1)
                {
                    warn.append('s');
                }
                warn.append(": [").append(supportedVersions).append("]");
                LOG.warn(warn.toString());

                // Per RFC 6455 - 4.4 - Supporting Multiple Versions of WebSocket Protocol
                // Using the examples as outlined
                response.setHeader("Sec-WebSocket-Version", supportedVersions);
                response.sendError(HttpStatus.BAD_REQUEST_400, "Unsupported websocket version specification");
                return false;
            }

            // Initialize / Negotiate Extensions
            ExtensionStack extensionStack = new ExtensionStack(extensionFactory);
            // The JSR allows for the extensions to be pre-negotiated, filtered, etc...
            // Usually from a Configurator.
            if (response.isExtensionsNegotiated())
            {
                // Use pre-negotiated extension list from response
                extensionStack.negotiate(response.getExtensions());
            }
            else
            {
                // Use raw extension list from request
                extensionStack.negotiate(request.getExtensions());
            }

            // Get original HTTP connection
            EndPoint endp = http.getEndPoint();
            Connector connector = http.getConnector();


            // Setup websocket connection
            AbstractWebSocketConnection wsConnection = new WebSocketServerConnection(
                endp, connector.getExecutor(), scheduler, driver.getPolicy(), connector.getByteBufferPool()
            );

            extensionStack.setPolicy(driver.getPolicy());
            extensionStack.configure(wsConnection.getParser());
            extensionStack.configure(wsConnection.getGenerator());

            if (LOG.isDebugEnabled())
            {
                LOG.debug("HttpConnection: {}", http);
                LOG.debug("WebSocketConnection: {}", wsConnection);
            }

            // Setup Session
            WebSocketSession session = createSession(request.getRequestURI(), driver, wsConnection);
            session.setUpgradeRequest(request);
            // set true negotiated extension list back to response
            response.setExtensions(extensionStack.getNegotiatedExtensions());
            session.setUpgradeResponse(response);
            wsConnection.addListener(session);

            // Setup Incoming Routing
            wsConnection.setNextIncomingFrames(extensionStack);
            extensionStack.setNextIncoming(session);

            // Setup Outgoing Routing
            session.setOutgoingHandler(extensionStack);
            extensionStack.setNextOutgoing(wsConnection);

            // Start Components
            session.addManaged(extensionStack);
            this.addManaged(session);

            if (session.isFailed())
            {
                throw new IOException("Session failed to start");
            }

            // Tell jetty about the new upgraded connection
            request.setServletAttribute(HttpConnection.UPGRADE_CONNECTION_ATTRIBUTE, wsConnection);

            if (LOG.isDebugEnabled())
                LOG.debug("Handshake Response: {}", handshaker);

            if (getSendServerVersion(connector))
                response.setHeader("Server", HttpConfiguration.SERVER_VERSION);

            // Process (version specific) handshake response
            handshaker.doHandshakeResponse(request, response);

            if (LOG.isDebugEnabled())
                LOG.debug("Websocket upgrade {} {} {} {}", request.getRequestURI(), version, response.getAcceptedSubProtocol(), wsConnection);

            return true;
        }

        private boolean getSendServerVersion(Connector connector)
        {
            ConnectionFactory connFactory = connector.getConnectionFactory(HttpVersion.HTTP_1_1.asString());
            if (connFactory == null)
                return false;

            if (connFactory instanceof HttpConnectionFactory)
            {
                HttpConfiguration httpConf = ((HttpConnectionFactory) connFactory).getHttpConfiguration();
                if (httpConf != null)
                    return httpConf.getSendServerVersion();
            }
            return false;
        }
    }
}