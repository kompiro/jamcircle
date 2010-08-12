package learning;

import java.security.Permission;

import org.junit.Test;

public class Learning {
	@Test
	public void doesnt_exit() throws Exception {
		System.setSecurityManager(new SecurityManager() {
			@Override
			public void checkExit(int status) {
				throw new SecurityException();
			}

			@Override
			public void checkPermission(final Permission perm) {
			}

			@Override
			public void checkPermission(final Permission perm, final Object context) {
			}
		});

		try {
			// 　はーい、ここ注目ですよー。
			System.exit(0);
		} catch (Exception e) {
		}
		System.out.println("System#exitを実行したのに終了しなかったYO!");

	}

}
