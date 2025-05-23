/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package jakarta.servlet.http;

import java.util.Set;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Build a request to be pushed.
 *
 * According section 8.2 of RFC 7540, a promised request must be cacheable and
 * safe without a request body.
 *
 * <p>A PushBuilder is obtained by calling {@link
 * HttpServletRequest#newPushBuilder()}.  Each call to this method will
 * a new instance of a PushBuilder based off the current {@code
 * HttpServletRequest}, or null.  Any mutations to the returned PushBuilder are
 * not reflected on future returns.</p>
 *
 * <p>The instance is initialized as follows:</p>
 *
 * <ul>
 *
 * <li>The method is initialized to "GET"</li>
 *
 * <li>The existing request headers of the current {@link HttpServletRequest}
 * are added to the builder, except for:
 *
 * <ul>
 *   <li>Conditional headers (defined in RFC 7232)
 *   <li>Range headers
 *   <li>Expect headers
 *   <li>Authorization headers
 *   <li>Referrer headers
 * </ul>
 *
 * </li>
 *
 * <li>If the request was authenticated, an Authorization header will
 * be set with a container generated token that will result in equivalent
 * Authorization for the pushed request.</li>
 *
 * <li>The session ID will be the value returned from {@link
 * HttpServletRequest#getRequestedSessionId()}, unless {@link
 * HttpServletRequest#getSession(boolean)} has previously been called to
 * create a new {@link HttpSession} prior to the call to create the
 * {@code PushBuilder}, in which case the new session ID will be used as
 * the PushBuilder's requested session ID.  Note that the session ID
 * returned from the request can effectively come from one of two
 * "sources": a cookie or the URL (as specified in {@link
 * HttpServletRequest#isRequestedSessionIdFromCookie} and {@link
 * HttpServletRequest#isRequestedSessionIdFromURL}, respectively).  The
 * session ID for the {@code PushBuilder} will also come from the same
 * source as the request.</li>
 *
 * <li>The Referer(sic) header will be set to {@link
 * HttpServletRequest#getRequestURL()} plus any {@link
 * HttpServletRequest#getQueryString()} </li>
 *
 * <li>If {@link HttpServletResponse#addCookie(Cookie)} has been called
 * on the associated response, then a corresponding Cookie header will be added
 * to the PushBuilder, unless the {@link Cookie#getMaxAge()} is &lt;=0, in which
 * case the Cookie will be removed from the builder.</li>
 *
 * </ul>
 *
 * <p>The {@link #path} method must be called on the {@code PushBuilder}
 * instance before the call to {@link #push}.  Failure to do so must
 * cause an exception to be thrown from {@link
 * #push}, as specified in that method.</p>
 *
 * <p>A PushBuilder can be customized by chained calls to mutator
 * methods before the {@link #push()} method is called to initiate an
 * asynchronous push request with the current state of the builder.
 * After the call to {@link #push()}, the builder may be reused for
 * another push, however the implementation must make it so the {@link
 * #path(String)} and conditional headers (defined in RFC 7232)
 * values are cleared before returning from {@link #push}.
 * All other values are retained over calls to {@link #push()}.
 *
 * @since Servlet 4.0
 */
public interface PushBuilder {
    /**
     * <p>Set the method to be used for the push.</p>
     *
     * @throws NullPointerException if the argument is {@code null}
     *
     * @throws IllegalArgumentException if the argument is the empty String,
     *         or any non-cacheable or unsafe methods defined in RFC 7231,
     *         which are POST, PUT, DELETE, CONNECT, OPTIONS and TRACE.
     *
     * @param method the method to be used for the push.
     * @return this builder.
     */
    public PushBuilder method(String method);

    /**
     * Set the query string to be used for the push.
     *
     * The query string will be appended to any query String included in a call
     * to {@link #path(String)}.  Any duplicate parameters must be preserved.
     * This method should be used instead of a query in {@link #path(String)}
     * when multiple {@link #push()} calls are to be made with the same
     * query string.
     * @param  queryString the query string to be used for the push.
     * @return this builder.
     */
    public PushBuilder queryString(String queryString);

    /**
     * Set the SessionID to be used for the push.
     * The session ID will be set in the same way it was on the associated request (ie
     * as a cookie if the associated request used a cookie, or as a url parameter if
     * the associated request used a url parameter).
     * Defaults to the requested session ID or any newly assigned session id from
     * a newly created session.
     * @param sessionId the SessionID to be used for the push.
     * @return this builder.
     */
    public PushBuilder sessionId(String sessionId);

    /**
     * <p>Set a request header to be used for the push.  If the builder has an
     * existing header with the same name, its value is overwritten.</p>
     *
     * @param name The header name to set
     * @param value The header value to set
     * @return this builder.
     */
    public PushBuilder setHeader(String name, String value);

    /**
     * <p>Add a request header to be used for the push.</p>
     * @param name The header name to add
     * @param value The header value to add
     * @return this builder.
     */
    public PushBuilder addHeader(String name, String value);

    /**
     * <p>Remove the named request header.  If the header does not exist, take
     * no action.</p>
     *
     * @param name The name of the header to remove
     * @return this builder.
     */
    public PushBuilder removeHeader(String name);

    /**
     * Set the URI path to be used for the push.  The path may start
     * with "/" in which case it is treated as an absolute path,
     * otherwise it is relative to the context path of the associated
     * request.  There is no path default and {@link #path(String)} must
     * be called before every call to {@link #push()}.  If a query
     * string is present in the argument {@code path}, its contents must
     * be merged with the contents previously passed to {@link
     * #queryString}, preserving duplicates.
     *
     * @param path the URI path to be used for the push, which may include a
     * query string.
     * @return this builder.
     */
    public PushBuilder path(String path);

    /**
     * Push a resource given the current state of the builder,
     * the method must be non-blocking.
     *
     * <p>Push a resource based on the current state of the PushBuilder.
     * Calling this method does not guarantee the resource will actually
     * be pushed, since it is possible the client can decline acceptance
     * of the pushed resource using the underlying HTTP/2 protocol.</p>
     *
     * <p>If the builder has a session ID, then the pushed request will
     * include the session ID either as a Cookie or as a URI parameter
     * as appropriate. The builders query string is merged with any
     * passed query string.</p>
     *
     * <p>Before returning from this method, the builder has its path,
     * conditional headers (defined in RFC 7232) nulled. All other fields
     * are left as is for possible reuse in another push.</p>
     *
     * @throws IllegalStateException if there was no call to {@link
     * #path} on this instance either between its instantiation or the
     * last call to {@code push()} that did not throw an
     * IllegalStateException.
     */
    public void push();

    /**
     * Return the method to be used for the push.
     *
     * @return the method to be used for the push.
     */
    public String getMethod();

    /**
     * Return the query string to be used for the push.
     *
     * @return the query string to be used for the push.
     */
    public String getQueryString();

    /**
     * Return the SessionID to be used for the push.
     *
     * @return the SessionID to be used for the push.
     */
    public String getSessionId();

    /**
     * Return the set of header to be used for the push.
     *
     * <p>The returned set is not backed by the {@code PushBuilder} object,
     * so changes in the returned set are not reflected in the
     * {@code PushBuilder} object, and vice-versa.</p>
     *
     * @return the set of header to be used for the push.
     */
    public Set<String> getHeaderNames();

    /**
     * Return the header of the given name to be used for the push.
     * @param name the name of the header
     *
     * @return the header of the given name to be used for the push.
     */
    public String getHeader(String name);

    /**
     * Return the URI path to be used for the push.
     *
     * @return the URI path to be used for the push.
     */
    public String getPath();
}
