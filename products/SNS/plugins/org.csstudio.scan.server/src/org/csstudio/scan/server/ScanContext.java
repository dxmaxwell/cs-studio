/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.server;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.csstudio.data.values.IValue;
import org.csstudio.scan.command.Comparison;
import org.csstudio.scan.condition.DeviceValueCondition;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceContext;
import org.csstudio.scan.device.ValueConverter;
import org.csstudio.scan.server.internal.ExecutableScan;

/** Context in which the {@link ScanCommandImpl}s of a {@link ExecutableScan} are executed.
 *
 *  <ul>
 *  <li>{@link Device}s with which commands can interact.
 *  <li>Methods to execute commands, supporting Pause/Continue/Abort
 *  <li>Data logger for {@link ScanSample}s
 *  <li>Progress information
 *  </ul>
 *
 *  <p>Scan commands can only interact with this restricted API
 *  of the actual {@link ExecutableScan}.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
abstract public class ScanContext
{
	private volatile boolean automatic_log_mode = false;

    final protected DeviceContext devices;

    final protected AtomicLong work_performed = new AtomicLong();

    public ScanContext(final DeviceContext devices)
    {
    	this.devices = devices;
    }

	/** Get a device by (alias) name
	 *  @param name
	 *  @return {@link Device} with that name
	 *  @throws Exception when device name not known
	 */
	public Device getDevice(final String name) throws Exception
    {
        return devices.getDeviceByAlias(name);
    }

    /** Write to device
     *  @param device_name Name of device
     *  @param value Value to write to the device
     *  @param readback Readback device
     *  @param wait Wait for readback to match?
     *  @param tolerance Numeric tolerance when checking value
     *  @param timeout Timeout in seconds, 0 as "forever"
     *  @throws Exception on error
     */
    public void write(final String device_name, final Object value, final String readback_name,
            final boolean wait, final double tolerance, final double timeout) throws Exception
    {
    	final Device device = getDevice(device_name);

    	// Separate read-back device, or use 'set' device?
    	final Device readback;
    	if (readback_name.isEmpty())
    	    readback = device;
    	else
    	    readback = getDevice(readback_name);

    	//  Wait for the device to reach the value?
    	final DeviceValueCondition condition;
    	if (wait)
    	{
    	    final double desired;
    	    if (value instanceof Number)
    	        desired = ((Number)value).doubleValue();
    	    else
    	        throw new Exception("Value must be numeric to support 'wait'");

    	    condition = new DeviceValueCondition(readback, Comparison.EQUALS, desired,
                    tolerance, timeout);
    	}
    	else
    	    condition = null;

    	// Perform write
    	device.write(value);

    	// Wait?
    	if (condition != null)
    	    condition.await();

    	// Log the value?
    	if (isAutomaticLogMode())
    	{
    		final IValue log_value = readback.read();
            final long serial = getNextScanDataSerial();
    		logSample(ValueConverter.createSample(readback.getInfo().getAlias(), serial, log_value));
    	}
    }

	/** Execute a list of commands
     *  @param commands {@link ScanCommandImpl}s to execute
     *  @throws Exception on error in executing a command
     */
    abstract public void execute(final List<ScanCommandImpl<?>> commands) throws Exception;

    /** Execute a single command
     *  @param command {@link ScanCommandImpl} to execute
     *  @throws Exception on error in executing the command
     */
    abstract public void execute(final ScanCommandImpl<?> command) throws Exception;

    /** Set log mode
     *  @param automatic Should commands automatically log every change that they perform/observe?
     */
    public void setLogMode(final boolean automatic)
    {
    	automatic_log_mode = automatic;
    }

    /** @return Should commands automatically log every change that they perform/observe?
     */
    public boolean isAutomaticLogMode()
    {
    	return automatic_log_mode;
    }

    /** @return Next unique {@link ScanSample} serial */
    abstract public long getNextScanDataSerial();

    /** @return {@link ScanData} of currently logged data or <code>null</code>
	 *  @throws Exception on error
     */
    abstract public ScanData getScanData() throws Exception;

    /** Log a sample, i.e. add it to the data set produced by the
	 *  scan
	 *  @param sample {@link ScanSample}
	 *  @throws Exception on error
	 */
    abstract public void logSample(final ScanSample sample) throws Exception;

	/** Inform scan context that work has been performed.
	 *  Meant to be called by {@link ScanCommandImpl}s
	 *  @param work_units Number of performed work units
	 */
    public void workPerformed(final int work_units)
    {
        work_performed.addAndGet(work_units);
    }
}
