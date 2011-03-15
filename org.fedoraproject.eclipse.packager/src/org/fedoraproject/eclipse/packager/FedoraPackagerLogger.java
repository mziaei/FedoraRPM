package org.fedoraproject.eclipse.packager;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fedoraproject.eclipse.packager.api.errors.FedoraPackagerAPIException;

/**
 * Log errors or informative messages to the Eclipse log
 * 
 */
public class FedoraPackagerLogger {
	
	/***/ public enum LogLevel {
		/**
		 * Show errors only in the log.
		 */
		ERROR,
		/**
		 * Show debug messages in the log.
		 */
		DEBUG
	}

	/**
	 * Plug-in specific debug log status code.
	 */
	public static final int DEBUG_STATUS = 3;
	/**
	 * Plug-in specific error log status code.
	 */
	public static final int ERROR_STATUS = 1;

	private ILog log;
	private LogLevel currentLogLevel;
	private static FedoraPackagerLogger instance;

	private FedoraPackagerLogger() {
		log = PackagerPlugin.getDefault().getLog();
		// default to error log level. i.e. show only errors.
		// TODO: Make this a preference or use DebugOptionsListener
		currentLogLevel = LogLevel.ERROR;
	}

	/**
	 * Get a FedoraPackagerLogger singleton.
	 * 
	 * @return The singleton instance.
	 */
	public static FedoraPackagerLogger getInstance() {
		if (instance == null) {
			instance = new FedoraPackagerLogger();
		}
		return instance;
	}

	/**
	 * Logs errors.
	 * 
	 * @param message
	 *            The human readable localized message.
	 * @param throwable
	 *            The exception which occurred.
	 */
	public void logError(String message, Throwable throwable) {
		// Always log errors
		log.log(new Status(IStatus.ERROR, PackagerPlugin.PLUGIN_ID,
				ERROR_STATUS, message, throwable));
	}

	/**
	 * Logs informative debug messages. Messages are only logged if debugging
	 * is turned on.
	 * 
	 * @param message
	 *            A human readable localized message.
	 */
	public void logInfo(String message) {
		if (currentLogLevel == LogLevel.DEBUG) {
			log.log(new Status(IStatus.INFO, PackagerPlugin.PLUGIN_ID, message));
		}
	}

	/**
	 * Logs informative debug messages. Messages are only logged if debugging
	 * is turned on.
	 * 
	 * @param message
	 *            A human readable localized message.
	 * @param reason
	 *            The exception indicating what really happened.
	 */
	public void logInfo(String message, FedoraPackagerAPIException reason) {
		if (currentLogLevel == LogLevel.DEBUG) {
			log.log(new Status(IStatus.INFO, PackagerPlugin.PLUGIN_ID,
					DEBUG_STATUS, message, reason));
		}
	}
}