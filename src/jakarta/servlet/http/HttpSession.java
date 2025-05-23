/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
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
 *
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jakarta.servlet.http;

import java.util.Enumeration;
import jakarta.servlet.ServletContext;

/**
 *
 * Provides a way to identify a user across more than one page
 * request or visit to a Web site and to store information about that user.
 *
 * <p>The servlet container uses this interface to create a session
 * between an HTTP client and an HTTP server. The session persists
 * for a specified time period, across more than one connection or
 * page request from the user. A session usually corresponds to one
 * user, who may visit a site many times. The server can maintain a
 * session in many ways such as using cookies or rewriting URLs.
 *
 * <p>This interface allows servlets to
 * <ul>
 * <li>View and manipulate information about a session, such as
 *     the session identifier, creation time, and last accessed time
 * <li>Bind objects to sessions, allowing user information to persist
 *     across multiple user connections
 * </ul>
 *
 * <p>When an application stores an object in or removes an object from a
 * session, the session checks whether the object implements
 * {@link HttpSessionBindingListener}. If it does,
 * the servlet notifies the object that it has been bound to or unbound
 * from the session. Notifications are sent after the binding methods complete.
 * For session that are invalidated or expire, notifications are sent after
 * the session has been invalidated or expired.
 *
 * <p> When container migrates a session between VMs in a distributed container
 * setting, all session attributes implementing the {@link HttpSessionActivationListener}
 * interface are notified.
 *
 * <p>A servlet should be able to handle cases in which
 * the client does not choose to join a session, such as when cookies are
 * intentionally turned off. Until the client joins the session,
 * <code>isNew</code> returns <code>true</code>.  If the client chooses
 * not to join
 * the session, <code>getSession</code> will return a different session
 * on each request, and <code>isNew</code> will always return
 * <code>true</code>.
 *
 * <p>Session information is scoped only to the current web application
 * (<code>ServletContext</code>), so information stored in one context
 * will not be directly visible in another.
 *
 * @author	Various
 *
 * @see 	HttpSessionBindingListener
 * @see 	HttpSessionContext
 */

public interface HttpSession {

    /**
     *
     * Returns the time when this session was created, measured
     * in milliseconds since midnight January 1, 1970 GMT.
     *
     * @return				a <code>long</code> specifying
     * 					when this session was created,
     *					expressed in
     *					milliseconds since 1/1/1970 GMT
     *
     * @exception IllegalStateException	if this method is called on an
     *					invalidated session
     */
    public long getCreationTime();


    /**
     * Returns a string containing the unique identifier assigned
     * to this session. The identifier is assigned
     * by the servlet container and is implementation dependent.
     *
     * @return				a string specifying the identifier
     *					assigned to this session
     */
    public String getId();


    /**
     *
     * Returns the last time the client sent a request associated with
     * this session, as the number of milliseconds since midnight
     * January 1, 1970 GMT, and marked by the time the container received the
     * request.
     *
     * <p>Actions that your application takes, such as getting or setting
     * a value associated with the session, do not affect the access
     * time.
     *
     * @return				a <code>long</code>
     *					representing the last time
     *					the client sent a request associated
     *					with this session, expressed in
     *					milliseconds since 1/1/1970 GMT
     *
     * @exception IllegalStateException	if this method is called on an
     *					invalidated session
     */
    public long getLastAccessedTime();


    /**
     * Returns the ServletContext to which this session belongs.
     *
     * @return The ServletContext object for the web application
     * @since Servlet 2.3
     */
    public ServletContext getServletContext();


    /**
     * Specifies the time, in seconds, between client requests before the
     * servlet container will invalidate this session.
     *
     * <p>An <tt>interval</tt> value of zero or less indicates that the
     * session should never timeout.
     *
     * @param interval		An integer specifying the number
     * 				of seconds
     */
    public void setMaxInactiveInterval(int interval);


    /**
     * Returns the maximum time interval, in seconds, that
     * the servlet container will keep this session open between
     * client accesses. After this interval, the servlet container
     * will invalidate the session.  The maximum time interval can be set
     * with the <code>setMaxInactiveInterval</code> method.
     *
     * <p>A return value of zero or less indicates that the
     * session will never timeout.
     *
     * @return		an integer specifying the number of
     *			seconds this session remains open
     *			between client requests
     *
     * @see		#setMaxInactiveInterval
     */
    public int getMaxInactiveInterval();



    public HttpSessionContext getSessionContext();


    /**
     * Returns the object bound with the specified name in this session, or
     * <code>null</code> if no object is bound under the name.
     *
     * @param name		a string specifying the name of the object
     *
     * @return			the object with the specified name
     *
     * @exception IllegalStateException	if this method is called on an
     *					invalidated session
     */
    public Object getAttribute(String name);


    /**
     * @param name		a string specifying the name of the object
     *
     * @return			the object with the specified name
     *
     * @exception IllegalStateException	if this method is called on an
     *					invalidated session
     */
    public Object getValue(String name);


    /**
     * Returns an <code>Enumeration</code> of <code>String</code> objects
     * containing the names of all the objects bound to this session.
     *
     * @return			an <code>Enumeration</code> of
     *				<code>String</code> objects specifying the
     *				names of all the objects bound to
     *				this session
     *
     * @exception IllegalStateException	if this method is called on an
     *					invalidated session
     */
    public Enumeration<String> getAttributeNames();


    /**
     * @return				an array of <code>String</code>
     *					objects specifying the
     *					names of all the objects bound to
     *					this session
     *
     * @exception IllegalStateException	if this method is called on an
     *					invalidated session
     */
    public String[] getValueNames();


    /**
     * Binds an object to this session, using the name specified.
     * If an object of the same name is already bound to the session,
     * the object is replaced.
     *
     * <p>After this method executes, and if the new object
     * implements <code>HttpSessionBindingListener</code>,
     * the container calls
     * <code>HttpSessionBindingListener.valueBound</code>. The container then
     * notifies any <code>HttpSessionAttributeListener</code>s in the web
     * application.

     * <p>If an object was already bound to this session of this name
     * that implements <code>HttpSessionBindingListener</code>, its
     * <code>HttpSessionBindingListener.valueUnbound</code> method is called.
     *
     * <p>If the value passed in is null, this has the same effect as calling
     * <code>removeAttribute()<code>.
     *
     *
     * @param name			the name to which the object is bound;
     *					cannot be null
     *
     * @param value			the object to be bound
     *
     * @exception IllegalStateException	if this method is called on an
     *					invalidated session
     */
    public void setAttribute(String name, Object value);


    /**
     * @deprecated 	As of Version 2.2, this method is
     * 			replaced by {@link #setAttribute}
     *
     * @param name			the name to which the object is bound;
     *					cannot be null
     *
     * @param value			the object to be bound; cannot be null
     *
     * @exception IllegalStateException	if this method is called on an
     *					invalidated session
     */
    @Deprecated
    public void putValue(String name, Object value);


    /**
     * Removes the object bound with the specified name from
     * this session. If the session does not have an object
     * bound with the specified name, this method does nothing.
     *
     * <p>After this method executes, and if the object
     * implements <code>HttpSessionBindingListener</code>,
     * the container calls
     * <code>HttpSessionBindingListener.valueUnbound</code>. The container
     * then notifies any <code>HttpSessionAttributeListener</code>s in the web
     * application.
     *
     * @param name				the name of the object to
     *						remove from this session
     *
     * @exception IllegalStateException	if this method is called on an
     *					invalidated session
     */
    public void removeAttribute(String name);


    /**
     * @param name				the name of the object to
     *						remove from this session
     *
     * @exception IllegalStateException	if this method is called on an
     *					invalidated session
     */
    public void removeValue(String name);


    /**
     * Invalidates this session then unbinds any objects bound
     * to it.
     *
     * @exception IllegalStateException	if this method is called on an
     *					already invalidated session
     */
    public void invalidate();


    /**
     * Returns <code>true</code> if the client does not yet know about the
     * session or if the client chooses not to join the session.  For
     * example, if the server used only cookie-based sessions, and
     * the client had disabled the use of cookies, then a session would
     * be new on each request.
     *
     * @return 				<code>true</code> if the
     *					server has created a session,
     *					but the client has not yet joined
     *
     * @exception IllegalStateException	if this method is called on an
     *					already invalidated session
     */
    public boolean isNew();

}

