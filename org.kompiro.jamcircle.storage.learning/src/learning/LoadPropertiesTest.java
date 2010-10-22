package learning;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ResourceBundle;

import org.junit.Test;

public class LoadPropertiesTest {
	@Test
	public void load() throws Exception {
		ResourceBundle bundle = ResourceBundle.getBundle("storage");
		assertThat(bundle.getString("testmode"), is("true"));
	}
}
