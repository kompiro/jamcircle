package org.kompiro.jamcircle;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.junit.*;
import org.kompiro.jamcircle.RCPUtils.AsyncRunnerDelegator;
import org.kompiro.jamcircle.RCPUtils.IAsyncRunnerDelegator;
import org.mockito.ArgumentCaptor;

public class RCPUtilsTest {

	private IAsyncRunnerDelegator backup;

	@Before
	public void before() throws Exception {
		RCPUtils.testmode = true;
		backup = RCPUtils.delegator;
	}

	@After
	public void after() throws Exception {
		RCPUtils.delegator = backup;
	}

	@Test
	public void expectedAsyncDelegator() throws Exception {
		assertThat(RCPUtils.delegator, notNullValue());
		assertThat(RCPUtils.delegator, instanceOf(AsyncRunnerDelegator.class));
	}

	@Test
	public void modifyAlphaForSurface() throws Exception {
		RCPUtils.delegator = new IAsyncRunnerDelegator() {
			public void run(Runnable runnable) {
				runnable.run();
			}
		};
		Shell shell = mock(Shell.class);
		RCPUtils.modifyAlphaForSurface(shell);
		ArgumentCaptor<Integer> cap = ArgumentCaptor.forClass(Integer.class);
		verify(shell, times(9)).setAlpha(cap.capture());
		int[] vals = new int[] { 0, 128, 192, 224, 240, 248, 252, 254, 255 };
		List<Integer> allValues = cap.getAllValues();
		for (int index = 0; index < vals.length; index++) {
			assertThat(allValues.get(index), is(vals[index]));
		}
	}

	@Test
	public void modifyAlphaForDropout() throws Exception {
		RCPUtils.delegator = new IAsyncRunnerDelegator() {
			public void run(Runnable runnable) {
				runnable.run();
			}
		};
		Shell shell = mock(Shell.class);
		RCPUtils.modifyAlphaForDropout(shell);
		ArgumentCaptor<Integer> cap = ArgumentCaptor.forClass(Integer.class);
		verify(shell, times(9)).setAlpha(cap.capture());
		int[] vals = new int[] { 255, 254, 252, 248, 240, 224, 192, 128, 0 };
		List<Integer> allValues = cap.getAllValues();
		for (int index = 0; index < vals.length; index++) {
			assertThat(allValues.get(index), is(vals[index]));
		}
	}

}
