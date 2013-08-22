/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.model;

import java.util.Calendar;
import java.util.logging.Level;

import org.csstudio.alarm.beast.msghist.Activator;
import org.csstudio.alarm.beast.msghist.rdb.MessageRDB;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;

/** Background job for getting messages from RDB.
 *  <p>
 *  The job actually connects to the RDB each time
 *  and disconnects when done to avoid timeouts with
 *  a long running RDB connection.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
abstract public class GetMessagesJob extends Job
{
	final private String url;
	final private String user;
	final private String password;
	final private String schema;
    final private Calendar start;
    final private Calendar end;
    final private MessagePropertyFilter[] filters;
    final private int max_properties;
    final private Shell shell;

    /** Initialize message job
     *  @param url RDB URL
     *  @param user RDB user
     *  @param password RDB password
     *  @param schema RDB schema
     *  @param start Start time
     *  @param end End time
     *  @param filters Message filters
     *  @param max_properties Max. message property count
     *  @param shell UI shell to display error dialog
     */
    public GetMessagesJob(
            final String url, final String user,
            final String password, final String schema,
            final Calendar start, final Calendar end,
            final MessagePropertyFilter filters[],
            final int max_properties, Shell shell)
    {
        super("Get Messages from RDB");
        this.url = url;
        this.user = user;
        this.password = password;
        this.schema = schema;
        this.start = start;
        this.end = end;
        this.filters = filters;
        this.max_properties = max_properties;
        this.shell = shell;
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor)
    {
        MessageRDB rdb = null;
        try
        {
            rdb = new MessageRDB(url, user, password, schema);
            final Message[] messages =
                rdb.getMessages(monitor, start, end, filters, max_properties);
            if (! monitor.isCanceled())
                gotMessages(messages);
        }
        catch (final Exception ex)
        {
        	handleError("Message Database Error", ex);
        }
        if (rdb != null)
            rdb.close();
        return Status.OK_STATUS;
    }

    /** Display error. Can be called from non-GUI thread
     */
    private void handleError(final String message, final Exception ex)
    {
    	Activator.getLogger().log(Level.WARNING, message, ex);
        if (shell == null) {
        	return;
        }
        if (shell.isDisposed()) {
            return;
        }
        shell.getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (shell.isDisposed()) {
                    return;
                }
                ExceptionDetailsErrorDialog.openError(shell, message, ex);
            }
        });
    }

    /** Derived class must implement to handle received messages */
    abstract void gotMessages(final Message[] messages);
}
