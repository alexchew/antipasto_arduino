	package antipasto;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import processing.app.Base;
import processing.app.debug.*;

/**
 * @author Christopher.Ladden
 *
 */
public class ScriptRunner implements MessageConsumer {
	private String binFileName = new String(" ");
	private RunnerException exception;
	private boolean isExecuting = false;
	private String[] envParams;
	private String[] envValues;
	private String scriptMessage = " ";

	/**
	 *  Create a generic script runner.
	 * @param winBinFileName The full path to the script interpreter on Windows.
	 * @param macBinFileName The full path to the script interpreter on Mac.
	 */
	public ScriptRunner(String winBinFileName, String macBinFileName) {

		if (Base.isMacOS() || Base.isLinux()) {
			this.binFileName = macBinFileName;
		} else {
			this.binFileName = winBinFileName;

		}
	}
	/**
	 *
	 * @return Returns the script interpreter's full path name.
	 */
	public String getBinFileName() {

		return this.binFileName;
	}

	/**
	 * Sets the System environment variables to
	 * be used while executing the script.
	 *
	 * @param envParams The list of parameters to set
	 * @param envValues The list of values for the given parameters
	 */
	public void setENVvariables(String[] envParams, String[] envValues) {
		this.envParams = envParams.clone();
		this.envValues = envValues.clone();
	}

	/**
	 *
	 * @return Returns the script's output messages.
	 */
	public String getScriptOutput() {
		return scriptMessage;
	}

	/**
	 * @param file The full path of the file to run.
	 * @return Nothing.
	 * @throws RunnerException
	 * @throws IOException
	 */
	public void run(String file) throws RunnerException, IOException {

		/* Build the command message */
		String[] command = {binFileName,
							file};

		/* Run the script */
		execAsynchronously(command, (new File(file)).getParent());

	}




	  public int execAsynchronously(String[] command, String workingDirectory)
	    throws RunnerException, IOException {

	    int result = 0;


      for(int j = 0; j < command.length; j++) {
        System.out.print(command[j] + " ");
      }
      System.out.println(" ");

      	ProcessBuilder processBuilder = new ProcessBuilder(command);
		Map env = processBuilder.environment();
		processBuilder.directory(new File(workingDirectory));

	    Properties prop = System.getProperties();
		env.put("CLASSPATH", prop.getProperty("java.class.path", null));
		env.put("JAVA_HOME", prop.getProperty("java.home", null));

	    Process process = processBuilder.start();

	    new MessageSiphon(process.getInputStream(), this);
	    new MessageSiphon(process.getErrorStream(), this);

	    // wait for the process to finish.  if interrupted
	    // before waitFor returns, continue waiting
	    isExecuting = true;
	      try {
	        result = process.waitFor();
	        isExecuting = false;
	      } catch (InterruptedException ignored) {
	      }

	      if (this.exception != null)  {
	          this.exception.hideStackTrace();
	          throw this.exception;
	        }

	    return result;
	  }

	/**
	 * Stores the script output messages locally.
	 */
	public void message(String s) {
		scriptMessage = scriptMessage + s;
	}

}
