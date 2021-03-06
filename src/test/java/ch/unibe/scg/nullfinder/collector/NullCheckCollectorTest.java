package ch.unibe.scg.nullfinder.collector;

import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.unibe.scg.nullfinder.jpa.entity.CompilationUnit;
import ch.unibe.scg.nullfinder.jpa.entity.NullCheck;

public class NullCheckCollectorTest {

	private CompilationUnitCollector compilationUnitCollector;
	private NullCheckCollector checkCollector;
	private List<NullCheck> nullChecks;

	@Before
	public void setUp() throws Exception {
		this.compilationUnitCollector = new CompilationUnitCollector();
		this.checkCollector = new NullCheckCollector();
		URL url = this.getClass().getResource("../TestNullClass.java");
		CompilationUnit compilationUnit = this.compilationUnitCollector
				.collect(Paths.get(url.toURI()));
		this.nullChecks = this.checkCollector.collect(compilationUnit);
	}

	@Test
	public void testSetUp() {
		Assert.assertEquals(this.nullChecks.stream().count(), 6);
	}

	@Test
	public void testFieldAccessNullCheck() {
		boolean found = this.nullChecks.stream().anyMatch(
				nullCheck -> nullCheck.getNode().getJavaParserNode().getParentNode()
						.toString().equals("this.field == null"));
		Assert.assertTrue(found);
	}

	@Test
	public void testArrayAccessNullCheck() {
		boolean found = this.nullChecks.stream().anyMatch(
				nullCheck -> nullCheck.getNode().getJavaParserNode().getParentNode()
						.toString().equals("array[0] == null"));
		Assert.assertTrue(found);
	}

	@Test
	public void testEnclosedNullCheck() {
		boolean found = this.nullChecks.stream().anyMatch(
				nullCheck -> nullCheck.getNode().getJavaParserNode().getParentNode()
						.toString().equals("(value = this) != null"));
		Assert.assertTrue(found);
	}

	@Test
	public void testCastNullCheck() {
		boolean found = this.nullChecks.stream().anyMatch(
				nullCheck -> nullCheck.getNode().getJavaParserNode().getParentNode()
						.toString().equals("(String) value != null"));
		Assert.assertTrue(found);
	}

	@Test
	public void testNameNullCheck() {
		boolean found = this.nullChecks.stream().anyMatch(
				nullCheck -> nullCheck.getNode().getJavaParserNode().getParentNode()
						.toString().equals("name == null"));
		Assert.assertTrue(found);
	}

	@Test
	public void testMethodCallNullCheck() {
		boolean found = this.nullChecks.stream().anyMatch(
				nullCheck -> nullCheck.getNode().getJavaParserNode().getParentNode()
						.toString().equals("this.getNull() == null"));
		Assert.assertTrue(found);
	}

}
