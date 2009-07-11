package annis.sqlgen;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import annis.model.Annotation;
import annis.model.AnnisNode.TextMatching;

public class TestSubQueryCorpusSelectionStrategy {

	private List<Long> corpusList;
	private List<Annotation> metaData;
	private SubQueryCorpusSelectionStrategy strategy;
	
	@Before
	public void setup() {
		corpusList = new ArrayList<Long>();
		metaData = new ArrayList<Annotation>();
		strategy = new SubQueryCorpusSelectionStrategy();
	}

	@Test
	public void hasCorpusSelectionFalse() {
		assertThat(strategy.hasCorpusSelection(corpusList, metaData), is(false));
	}
	
	@Test
	public void hasCorpusSelectionCorpusList() {
		corpusList = Arrays.asList(23L);
		assertThat(strategy.hasCorpusSelection(corpusList, metaData), is(true));
	}
	
	@Test
	public void hasCorpusSelectionMetaData() {
		metaData = Arrays.asList(new Annotation("NAMESPACE", "NAME"));
		assertThat(strategy.hasCorpusSelection(corpusList, metaData), is(true));
	}
	
	@Test
	public void buildSubQueryOneCorpus() {
		String expected = "" +
				"SELECT DISTINCT c1.id " +
				"FROM corpus AS c1, corpus AS c2 " +
				"WHERE c1.pre >= c2.pre " +
				"AND c1.post <= c2.post " +
				"AND c2.id IN ( 23 )";
		corpusList = Arrays.asList(23L);
		assertEquals(expected, strategy.buildSubQuery(corpusList, metaData));
	}
	
	@Test
	public void buildSubQueryManyCorpus() {
		String expected = "" +
				"SELECT DISTINCT c1.id " +
				"FROM corpus AS c1, corpus AS c2 " +
				"WHERE c1.pre >= c2.pre " +
				"AND c1.post <= c2.post " +
				"AND c2.id IN ( 23, 42, 69 )";
		corpusList = Arrays.asList(23L, 42L, 69L);
		assertEquals(expected, strategy.buildSubQuery(corpusList, metaData));
	}
	
	@Test
	public void buildSubQueryEmptyCorpusList() {
		String expected = "SELECT DISTINCT c1.id FROM corpus AS c1";
		assertEquals(expected, strategy.buildSubQuery(corpusList, metaData));
	}
	
	@Test
	public void corpusConstraintEmptyCorpusListAnnotation() {
		String expected = "" +
			"SELECT DISTINCT c1.id " +
			"FROM corpus AS c1, corpus_annotation AS corpus_annotation1, corpus_annotation AS corpus_annotation2, corpus_annotation AS corpus_annotation3 " +
			"WHERE corpus_annotation1.namespace = 'namespace1' " +
			"AND corpus_annotation1.name = 'name1' " +
			"AND corpus_annotation1.corpus_ref = c1.id " +
			"AND corpus_annotation2.namespace = 'namespace2' " +
			"AND corpus_annotation2.name = 'name2' " +
			"AND corpus_annotation2.value ~=~ 'value2' " +
			"AND corpus_annotation2.corpus_ref = c1.id " +
			"AND corpus_annotation3.namespace = 'namespace3' " +
			"AND corpus_annotation3.name = 'name3' " +
			"AND corpus_annotation3.value ~ 'value3' " +
			"AND corpus_annotation3.corpus_ref = c1.id";
		
		Annotation annotation1 = new Annotation("namespace1", "name1");
		Annotation annotation2 = new Annotation("namespace2", "name2", "value2", TextMatching.EXACT);
		Annotation annotation3 = new Annotation("namespace3", "name3", "value3", TextMatching.REGEXP);
		
		metaData = Arrays.asList(annotation1, annotation2, annotation3);
		assertEquals(expected, strategy.buildSubQuery(corpusList, metaData));
	}
	
	@Test
	public void corpusConstraintCorpusListAndAnnotation() {
		String expected = "" +
			"SELECT DISTINCT c1.id " +
			"FROM corpus AS c1, corpus AS c2, corpus_annotation AS corpus_annotation1, corpus_annotation AS corpus_annotation2, corpus_annotation AS corpus_annotation3 " +
			"WHERE c1.pre >= c2.pre " +
			"AND c1.post <= c2.post " +
			"AND c2.id IN ( 23, 42, 69 ) " +
			"AND corpus_annotation1.namespace = 'namespace1' " +
			"AND corpus_annotation1.name = 'name1' " +
			"AND corpus_annotation1.corpus_ref = c1.id " +
			"AND corpus_annotation2.namespace = 'namespace2' " +
			"AND corpus_annotation2.name = 'name2' " +
			"AND corpus_annotation2.value ~=~ 'value2' " +
			"AND corpus_annotation2.corpus_ref = c1.id " +
			"AND corpus_annotation3.namespace = 'namespace3' " +
			"AND corpus_annotation3.name = 'name3' " +
			"AND corpus_annotation3.value ~ 'value3' " +
			"AND corpus_annotation3.corpus_ref = c1.id";
		
		Annotation annotation1 = new Annotation("namespace1", "name1");
		Annotation annotation2 = new Annotation("namespace2", "name2", "value2", TextMatching.EXACT);
		Annotation annotation3 = new Annotation("namespace3", "name3", "value3", TextMatching.REGEXP);
		
		corpusList = Arrays.asList(23L, 42L, 69L);
		metaData = Arrays.asList(annotation1, annotation2, annotation3);
		assertEquals(expected, strategy.buildSubQuery(corpusList, metaData));
	}
	
}
