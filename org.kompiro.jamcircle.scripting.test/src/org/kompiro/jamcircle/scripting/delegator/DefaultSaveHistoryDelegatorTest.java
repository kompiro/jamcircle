package org.kompiro.jamcircle.scripting.delegator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DefaultSaveHistoryDelegatorTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void store_one_line() throws Exception {
		File target = folder.newFile("temp.file");
		DefaultSaveHistoryDelegator delegator = new DefaultSaveHistoryDelegator();
		delegator.delegate(target, "p 'test'");
		List<String> lines = readLines(target);
		assertThat(lines.size(), is(1));
		assertThat(lines.get(0), is("p 'test'"));
	}

	@Test
	public void store_multi_lines() throws Exception {
		File target = folder.newFile("temp.file");
		DefaultSaveHistoryDelegator delegator = new DefaultSaveHistoryDelegator();
		delegator.delegate(target, "p 'test'\r\np 'test2'");
		List<String> lines = readLines(target);
		assertThat(lines.size(), is(2));
		assertThat(lines.get(0), is("p 'test'"));
		assertThat(lines.get(1), is("p 'test2'"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void store_target_is_null() throws Exception {
		File null_value = null;
		String not_test_value = "dadada...";
		DefaultSaveHistoryDelegator delegator = new DefaultSaveHistoryDelegator();
		delegator.delegate(null_value, not_test_value);
	}

	@Test(expected = IllegalArgumentException.class)
	public void store_histories_is_null() throws Exception {
		String null_value = null;
		File not_test_value = folder.newFile("dadada...");
		DefaultSaveHistoryDelegator delegator = new DefaultSaveHistoryDelegator();
		delegator.delegate(not_test_value, null_value);
	}

	@Test
	public void overwrite_existed_file() throws Exception {
		File existed = folder.newFile("existed.file");

		List<String> initial = readLines(existed);
		assertThat(initial.size(), is(0));
		File target = new File(existed.getAbsolutePath());

		String histories = "p 'test'";
		DefaultSaveHistoryDelegator delegator = new DefaultSaveHistoryDelegator();
		delegator.delegate(target, histories);
		List<String> actual = readLines(target);
		assertThat(actual.size(), is(1));
		assertThat(actual.get(0), is("p 'test'"));
	}

	private List<String> readLines(File target) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(target));
		String line = null;
		List<String> lines = new ArrayList<String>();
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		return lines;
	}

}
