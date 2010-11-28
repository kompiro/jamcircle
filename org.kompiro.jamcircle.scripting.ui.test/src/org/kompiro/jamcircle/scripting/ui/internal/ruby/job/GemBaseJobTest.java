package org.kompiro.jamcircle.scripting.ui.internal.ruby.job;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.console.*;
import org.junit.Test;
import org.kompiro.jamcircle.scripting.util.JRubyUtil;
import org.mockito.ArgumentCaptor;

public class GemBaseJobTest {

	public class GemBaseJobForTest extends GemBaseJob {

		private String os;

		private GemBaseJobForTest(String os) {
			this.os = os;
		}

		@Override
		protected String getCommand() {
			return "job";
		}

		@Override
		protected void runProcessStart(ProcessBuilder builder, IOConsoleOutputStream outputStream,
				IOConsoleOutputStream errorStream) {
			// do nothing for testing
		}

		@Override
		protected void setColor(IOConsoleOutputStream outputStream, IOConsoleOutputStream errorStream) {
			// do nothing for testing
		}

		@Override
		protected String getOS() {
			return os;
		}

		@Override
		protected IOConsole createConsole() {
			IOConsole console = mock(IOConsole.class);
			when(console.getName()).thenReturn(GemBaseJob.CONSOLE_NAME_OF_GEM);
			return console;
		}
	}

	@Test
	public void run_jgem_command() throws Exception {

		String targetOS = "test";
		GemBaseJob job = initialize(targetOS);
		job.setTarget("target");

		job.run(new NullProgressMonitor());

		ArgumentCaptor<ProcessBuilder> captor = ArgumentCaptor.forClass(ProcessBuilder.class);
		verify(job).runProcessStart(captor.capture(), (IOConsoleOutputStream) any(), (IOConsoleOutputStream) any());

		List<String> commands = captor.getValue().command();
		assertThat(commands.get(0), is("/bin/jgem"));
		assertThat(commands.get(1), is("job"));

	}

	@Test
	public void run_jgem_command_with_target_when_set_target() throws Exception {

		String targetOS = "test";
		GemBaseJob job = initialize(targetOS);
		job.setTarget("target");

		job.run(new NullProgressMonitor());

		ArgumentCaptor<ProcessBuilder> captor = ArgumentCaptor.forClass(ProcessBuilder.class);
		verify(job).runProcessStart(captor.capture(), (IOConsoleOutputStream) any(), (IOConsoleOutputStream) any());

		List<String> commands = captor.getValue().command();
		assertThat(commands.get(0), is("/bin/jgem"));
		assertThat(commands.get(1), is("job"));
		assertThat(commands.get(2), is("target"));

	}

	@Test
	public void run_jgem_command_when_not_run_on_MacOSX() throws Exception {

		String targetOS = Platform.OS_MACOSX;
		GemBaseJob job = initialize(targetOS);

		job.run(new NullProgressMonitor());

		ArgumentCaptor<ProcessBuilder> captor = ArgumentCaptor.forClass(ProcessBuilder.class);
		verify(job).runProcessStart(captor.capture(), (IOConsoleOutputStream) any(), (IOConsoleOutputStream) any());
		assertThat(captor.getValue().command().get(0), is("/bin/jgem"));

	}

	@Test
	public void run_jgem_command_when_not_run_on_Linux() throws Exception {

		String targetOS = Platform.OS_LINUX;
		GemBaseJob job = initialize(targetOS);

		job.run(new NullProgressMonitor());

		ArgumentCaptor<ProcessBuilder> captor = ArgumentCaptor.forClass(ProcessBuilder.class);
		verify(job).runProcessStart(captor.capture(), (IOConsoleOutputStream) any(), (IOConsoleOutputStream) any());
		assertThat(captor.getValue().command().get(0), is("/bin/jgem"));

	}

	@Test
	public void run_jgem_command_when_not_run_on_Windows() throws Exception {

		String targetOS = Platform.OS_WIN32;
		GemBaseJob job = initialize(targetOS);

		job.run(new NullProgressMonitor());

		ArgumentCaptor<ProcessBuilder> captor = ArgumentCaptor.forClass(ProcessBuilder.class);
		verify(job).runProcessStart(captor.capture(), (IOConsoleOutputStream) any(), (IOConsoleOutputStream) any());
		assertThat(captor.getValue().command().get(0), is("/bin/jgem.bat"));

	}

	@Test
	public void open_only_one_console_when_multiple_running() throws Exception {

		GemBaseJob job = new GemBaseJobForTest("test");

		IConsoleManager consoleManager = mock(IConsoleManager.class);
		job.setConsoleManager(consoleManager);
		when(consoleManager.getConsoles()).thenReturn(new IConsole[] {});

		JRubyUtil jRubyUtil = mock(JRubyUtil.class, withSettings().defaultAnswer(RETURNS_SMART_NULLS));
		job.setjRubyUtil(jRubyUtil);

		job.run(new NullProgressMonitor());
		verify(consoleManager, times(1)).addConsoles((IConsole[]) any());

		IOConsole console = mock(IOConsole.class);
		when(console.getName()).thenReturn(GemBaseJob.CONSOLE_NAME_OF_GEM);
		when(consoleManager.getConsoles()).thenReturn(new IConsole[] { console });

		job.run(new NullProgressMonitor());
		verify(consoleManager, times(1)).addConsoles((IConsole[]) any());
	}

	private GemBaseJob initialize(String targetOS) {
		GemBaseJob base = new GemBaseJobForTest(targetOS);
		GemBaseJob job = spy(base);

		IConsoleManager consoleManager = mock(IConsoleManager.class);
		job.setConsoleManager(consoleManager);
		when(consoleManager.getConsoles()).thenReturn(new IConsole[] {});

		JRubyUtil jRubyUtil = mock(JRubyUtil.class, withSettings().defaultAnswer(RETURNS_SMART_NULLS));
		job.setjRubyUtil(jRubyUtil);
		return job;
	}

}
