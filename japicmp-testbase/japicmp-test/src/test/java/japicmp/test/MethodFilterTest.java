package japicmp.test;

import japicmp.JApiCmp;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.filter.JavadocLikeBehaviorFilter;
import japicmp.model.JApiClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;

import java.util.List;

import static japicmp.test.util.Helper.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MethodFilterTest {
	@Rule
	public final StandardOutputStreamLog log = new StandardOutputStreamLog();

	@Test
	public void testMethodIsExcluded() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getExcludes().add(new JavadocLikeBehaviorFilter(MethodFilter.class.getName() + "#methodToExclude()"));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		JApiClass jApiClass = getJApiClass(jApiClasses, MethodFilter.class.getName());
		assertThat(jApiClass, hasJApiMethodWithName("methodToInclude"));
		assertThat(jApiClass, hasNoJApiMethodWithName("methodToExclude"));
	}

	@Test
	public void testMethodIsIncluded() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getIncludes().add(new JavadocLikeBehaviorFilter(MethodFilter.class.getName() + "#methodToInclude()"));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		assertThat(jApiClasses.size(), is(1));
		JApiClass jApiClass = getJApiClass(jApiClasses, MethodFilter.class.getName());
		assertThat(jApiClass, hasJApiMethodWithName("methodToInclude"));
		assertThat(jApiClass, hasNoJApiMethodWithName("methodToExclude"));
	}

	@Test
	public void testMethodIsIncludedWithApp() {
		JApiCmp.main(new String[]{"--include", MethodFilter.class.getName() + "#methodToInclude();" + Methods.class.getName() + "#finalToNonFinalMethod()", "-o", getArchive("japicmp-test-v1.jar").getAbsolutePath(), "-n", getArchive("japicmp-test-v2.jar").getAbsolutePath()});
		String log = this.log.getLog();
		assertThat(log, containsString(MethodFilter.class.getName()));
		assertThat(log, containsString("methodToInclude"));
		assertThat(log, containsString(Methods.class.getName()));
		assertThat(log, containsString("finalToNonFinalMethod"));
	}
}
