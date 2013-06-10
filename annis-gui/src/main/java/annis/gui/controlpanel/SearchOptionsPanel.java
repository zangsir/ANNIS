/*
 * Copyright 2011 Corpuslinguistic working group Humboldt University Berlin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package annis.gui.controlpanel;

import annis.libgui.Helper;
import annis.gui.components.HelpButton;
import static annis.gui.controlpanel.SearchOptionsPanel.NULL_SEGMENTATION_VALUE;
import annis.service.objects.AnnisAttribute;
import annis.service.objects.CorpusConfig;
import annis.service.objects.CorpusConfigMap;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author thomas
 * @author Benjamin Weißenfels <b.pixeldrama@gmail.com>
 */
public class SearchOptionsPanel extends FormLayout
{

  public static final String KEY_DEFAULT_SEGMENTATION = "default-text-segmentation";

  public static final String NULL_SEGMENTATION_VALUE = "tokens (default)";

  public static final String KEY_MAX_CONTEXT_LEFT = "max-context-left";

  public static final String KEY_MAX_CONTEXT_RIGHT = "max-context-right";

  public static final String KEY_CONTEXT_STEPS = "context-steps";

  public static final String KEY_DEFAULT_CONTEXT = "default-context";

  public static final String KEY_RESULT_PER_PAGE = "results-per-page";

  public static final String DEFAULT_CONFIG = "default-config";

  private static final Logger log = LoggerFactory.getLogger(
    SearchOptionsPanel.class);

  /**
   * Holds all available corpus configuarations, including the defautl
   * configeruation.
   *
   * The default configuration is available with the key "default-config"
   */
  private CorpusConfigMap corpusConfigurations;

  private ComboBox cbLeftContext;

  private ComboBox cbRightContext;

  private ComboBox cbResultsPerPage;

  private ComboBox cbSegmentation;
  // TODO: make this configurable

  private static final Integer[] PREDEFINED_PAGE_SIZES = new Integer[]
  {
    1, 2, 5, 10, 20, 25
  };

  static final Integer[] PREDEFINED_CONTEXTS = new Integer[]
  {
    1, 2, 5, 10, 20
  };

  /**
   * Caches all calculated corpus configurations. Note, also multiple selection
   * are stored. The keys for this kind of selection are generated by
   * {@link #buildKey()}.
   */
  private Map<String, CorpusConfig> lastSelection;

  public SearchOptionsPanel()
  {
    setWidth("99%");
    setHeight("-1px");

    addStyleName("contextsensible-formlayout");

    // init the config cache
    lastSelection = new HashMap<String, CorpusConfig>();


    cbLeftContext = new ComboBox("Left Context");
    cbRightContext = new ComboBox("Right Context");
    cbResultsPerPage = new ComboBox("Results Per Page");

    cbLeftContext.setNullSelectionAllowed(false);
    cbRightContext.setNullSelectionAllowed(false);
    cbResultsPerPage.setNullSelectionAllowed(false);
//
    cbLeftContext.setNewItemsAllowed(true);
    cbRightContext.setNewItemsAllowed(true);
    cbResultsPerPage.setNewItemsAllowed(true);
//
    cbLeftContext.setTextInputAllowed(true);
    cbRightContext.setTextInputAllowed(true);
    cbResultsPerPage.setTextInputAllowed(true);

//    cbLeftContext.setImmediate(true);
//    cbRightContext.setImmediate(true);
//    cbResultsPerPage.setImmediate(true);

//    cbLeftContext.addValidator(new IntegerRangeValidator("must be a number",
//      Integer.MIN_VALUE, Integer.MAX_VALUE));
//    cbRightContext.addValidator(new IntegerRangeValidator("must be a number",
//      Integer.MIN_VALUE, Integer.MAX_VALUE));
//    cbResultsPerPage.addValidator(new IntegerRangeValidator("must be a number",
//      Integer.MIN_VALUE, Integer.MAX_VALUE));

    cbSegmentation = new ComboBox("Show context in");
    cbSegmentation.setTextInputAllowed(false);
    cbSegmentation.setNullSelectionAllowed(true);

    cbSegmentation.
      setDescription("If corpora with multiple "
      + "context definitions are selected, a list of available context units will be "
      + "displayed. By default context is calculated in ‘tokens’ "
      + "(e.g. 5 minimal units to the left and right of a search result). "
      + "Some corpora might offer further context definitions, e.g. in "
      + "syllables, word forms belonging to different speakers, normalized or "
      + "diplomatic segmentations of a manuscript, etc.");

    addComponent(cbLeftContext);
    addComponent(cbRightContext);
    addComponent(new HelpButton(cbSegmentation));

    addComponent(cbResultsPerPage);

    corpusConfigurations = Helper.getCorpusConfigs();

    Integer resultsPerPage = Integer.parseInt(corpusConfigurations.get(
      DEFAULT_CONFIG).getConfig(KEY_RESULT_PER_PAGE));
    Integer leftCtx = Integer.parseInt(corpusConfigurations.get(DEFAULT_CONFIG).
      getConfig(KEY_MAX_CONTEXT_LEFT));
    Integer rightCtx = Integer.parseInt(
      corpusConfigurations.get(DEFAULT_CONFIG).getConfig(KEY_MAX_CONTEXT_RIGHT));
    Integer defaultCtx = Integer.parseInt(corpusConfigurations.get(
      DEFAULT_CONFIG).getConfig(KEY_DEFAULT_CONTEXT));
    Integer ctxSteps = Integer.parseInt(
      corpusConfigurations.get(DEFAULT_CONFIG).getConfig(KEY_CONTEXT_STEPS));
    String segment = corpusConfigurations.get(DEFAULT_CONFIG).getConfig(
      KEY_DEFAULT_SEGMENTATION);

    updateContext(cbLeftContext, leftCtx, ctxSteps, defaultCtx);
    updateContext(cbRightContext, rightCtx, ctxSteps, defaultCtx);
    updateSegmentations(null, segment);

    updateResultsPerPage(resultsPerPage);
  }

  public void updateSearchPanelConfiguration(Set<String> corpora)
  {
    // check if a configuration is already calculated
    String key = buildKey(corpora);
    if (!lastSelection.containsKey(key))
    {
      lastSelection.put(key, generateConfig(corpora));
    }


    Integer leftCtx = Integer.parseInt(lastSelection.get(key).getConfig(
      KEY_MAX_CONTEXT_LEFT));
    Integer rightCtx = Integer.parseInt(lastSelection.get(key).getConfig(
      KEY_MAX_CONTEXT_RIGHT));
    Integer defaultCtx = Integer.parseInt(lastSelection.get(key).getConfig(
      KEY_DEFAULT_CONTEXT));
    Integer ctxSteps = Integer.parseInt(lastSelection.get(key).getConfig(
      KEY_CONTEXT_STEPS));
    Integer resultsPerPage = Integer.parseInt(lastSelection.get(key).getConfig(
      KEY_RESULT_PER_PAGE));
    String segment = lastSelection.get(key).getConfig(KEY_DEFAULT_SEGMENTATION);

    // update the left and right context
    updateContext(cbLeftContext, leftCtx, ctxSteps, defaultCtx);
    updateContext(cbRightContext, rightCtx, ctxSteps, defaultCtx);
    updateResultsPerPage(resultsPerPage);

    updateSegmentations(corpora, segment);

  }

  private void updateSegmentations(Set<String> corpora, String segment)
  {

    cbSegmentation.removeAllItems();
    cbSegmentation.setNullSelectionItemId(NULL_SEGMENTATION_VALUE);
    cbSegmentation.addItem(NULL_SEGMENTATION_VALUE);

    if (segment.equalsIgnoreCase("tok"))
    {
      cbSegmentation.setValue(NULL_SEGMENTATION_VALUE);
    }
    else
    {
      cbSegmentation.addItem(segment);
      cbSegmentation.setValue(segment);
    }

    if (corpora != null && !corpora.isEmpty())
    {
      // get all segmentation paths
      WebResource service = Helper.getAnnisWebResource();
      if (service != null)
      {

        List<AnnisAttribute> attributes = new LinkedList<AnnisAttribute>();

        for (String corpus : corpora)
        {
          try
          {
            attributes.addAll(
              service.path("query").path("corpora").path(URLEncoder.encode(
              corpus,
              "UTF-8"))
              .path("annotations").queryParam(
              "fetchvalues", "true").
              queryParam("onlymostfrequentvalues", "true").
              get(new AnnisAttributeListType()));
          }
          catch (UnsupportedEncodingException ex)
          {
            log.error(null, ex);
          }
        }


        for (AnnisAttribute att : attributes)
        {
          if (AnnisAttribute.Type.segmentation == att.getType()
            && att.getName() != null
            && !att.getName().equalsIgnoreCase(segment))
          {
            cbSegmentation.addItem(att.getName());
          }
        }
      }
    }
  }

  /**
   * If all values of a specific corpus property have the same value, this value
   * is returned, otherwise the value from the default config is choosen.
   *
   * @param key The property key.
   * @param corpora Specifies the selected corpora.
   * @return A value defined in the copurs.properties file or in the
   * admin-service.properties
   */
  private String theGreatestCommonDenominator(String key, Set<String> corpora)
  {
    int value = -1;

    for (String corpus : corpora)
    {

      try
      {
        if (corpus.equals(Helper.DEFAULT_CONFIG))
        {
          continue;
        }

        if (!corpusConfigurations.get(corpus).getConfig().containsKey(key))
        {
          value = Integer.parseInt(
            corpusConfigurations.get(Helper.DEFAULT_CONFIG).getConfig().
            getProperty("default-context"));
          break;
        }

        int tmp = Integer.parseInt(corpusConfigurations.get(corpus).getConfig().
          getProperty(key));
        if (value < 0)
        {
          value = tmp;
        }

        if (value != tmp)
        {
          value = Integer.parseInt(
            corpusConfigurations.get(Helper.DEFAULT_CONFIG).getConfig().
            getProperty(key));
        }
      }
      catch (NumberFormatException ex)
      {
        log.error(
          "Cannot parse the string to an integer for key {} in corpus {} config",
          key, corpus, ex);
      }
    }

    return String.valueOf(value);
  }

  public void setLeftContext(int context)
  {
    cbLeftContext.setValue("" + context);
  }

  public int getLeftContext()
  {
    int result = 5;
    try
    {
      result = (Integer) cbLeftContext.getValue();
    }
    catch (NumberFormatException ex)
    {
      log.warn("Invalid integer submitted to search options ComboBox", ex);
    }

    return Math.max(0, result);
  }

  public int getRightContext()
  {
    int result = 5;
    try
    {
      result = (Integer) cbRightContext.getValue();
    }
    catch (NumberFormatException ex)
    {
      log.warn("Invalid integer submitted to search options ComboBox", ex);
    }

    return Math.max(0, result);
  }

  public void setRightContext(int context)
  {
    cbRightContext.setValue("" + context);
  }

  public int getResultsPerPage()
  {
    int result = 10;
    try
    {
      result = (Integer) cbResultsPerPage.getValue();
    }
    catch (NumberFormatException ex)
    {
      log.warn("Invalid integer submitted to search options ComboBox", ex);
    }

    return Math.max(0, result);
  }

  public String getSegmentationLayer()
  {
    return (String) cbSegmentation.getValue();
  }

  public void setSegmentationLayer(String layer)
  {
    cbSegmentation.setValue(layer);
  }

  /**
   * Builds a config for selection of one or muliple corpora.
   *
   * @param corpora Specifies the combination of corpora, for which the config
   * is calculated.
   * @return A new config which takes into account the segementation of all
   * selected corpora.
   */
  private CorpusConfig generateConfig(Set<String> corpora)
  {
    corpusConfigurations = Helper.getCorpusConfigs();
    CorpusConfig corpusConfig = new CorpusConfig();

    // calculate the left and right context.
    String leftCtx = theGreatestCommonDenominator(KEY_MAX_CONTEXT_LEFT, corpora);
    String rightCtx = theGreatestCommonDenominator(KEY_MAX_CONTEXT_RIGHT,
      corpora);
    corpusConfig.setConfig(KEY_MAX_CONTEXT_LEFT, leftCtx);
    corpusConfig.setConfig(KEY_MAX_CONTEXT_RIGHT, rightCtx);

    // calculate the default-context
    corpusConfig.setConfig(KEY_CONTEXT_STEPS, theGreatestCommonDenominator(
      KEY_CONTEXT_STEPS, corpora));
    corpusConfig.setConfig(KEY_DEFAULT_CONTEXT, theGreatestCommonDenominator(
      KEY_DEFAULT_CONTEXT, corpora));

    // get the results per page
    corpusConfig.setConfig(KEY_RESULT_PER_PAGE, theGreatestCommonDenominator(
      KEY_RESULT_PER_PAGE, corpora));

    corpusConfig.setConfig(KEY_DEFAULT_SEGMENTATION, checkSegments(corpora));

    return corpusConfig;
  }

  /**
   * Checks, if all selected corpora have the same default segmentation layer.
   * If not the tok layer is taken, because every corpus has this one.
   *
   * @param corpora the corpora which has to be checked.
   * @return "tok" or a segment which is defined in all corpora.
   */
  private String checkSegments(Set<String> corpora)
  {
    String segmentation = null;
    for (String corpus : corpora)
    {
      String tmpSegment = corpusConfigurations.get(corpus).getConfig(
        KEY_DEFAULT_SEGMENTATION);

      /**
       * If no segment is set in the corpus config use always the tok segment.
       */
      if (tmpSegment == null)
      {
        return corpusConfigurations.get(DEFAULT_CONFIG).getConfig(
          KEY_DEFAULT_SEGMENTATION);
      }

      if (segmentation == null)
      {
        segmentation = tmpSegment;
        continue;
      }

      if (!segmentation.equals(tmpSegment)) // return the default config
      {
        return corpusConfigurations.get(DEFAULT_CONFIG).getConfig(
          KEY_DEFAULT_SEGMENTATION);
      }
    }

    return segmentation;
  }

  private void updateResultsPerPage(Integer resultsPerPage)
  {
    // update result per page
    cbResultsPerPage.removeAllItems();

    Set<Integer> tmpResultsPerPage = new TreeSet<Integer>();
    for (Integer i : PREDEFINED_PAGE_SIZES)
    {
      tmpResultsPerPage.add(i);
    }

    tmpResultsPerPage.add(resultsPerPage);

    for (Integer i : tmpResultsPerPage)
    {
      cbResultsPerPage.addItem(i);
    }

    cbResultsPerPage.setValue(resultsPerPage);
    // /update result per page
  }

  private void updateContext(ComboBox c, int maxCtx, int ctxSteps,
    int defaultCtx)
  {
    // update left and right context
    c.removeAllItems();



    /**
     * The sorting via index container is much to complex for me, so I sort the
     * items first and put them afterwards into the combo boxes.
     */
    SortedSet<Integer> steps = new TreeSet<Integer>();

    for (Integer i : PREDEFINED_CONTEXTS)
    {
      if (i < maxCtx)
      {
        steps.add(i);
      }
    }

    for (int step = ctxSteps; step < maxCtx; step += ctxSteps)
    {
      steps.add(step);
    }

    steps.add(maxCtx);
    steps.add(defaultCtx);

    for (Integer i : steps)
    {
      c.addItem(i);
    }

    c.setValue(defaultCtx);
  }

  private static class AnnisAttributeListType extends GenericType<List<AnnisAttribute>>
  {

    public AnnisAttributeListType()
    {
    }
  }

  /**
   * Builds a Key for {@link #lastSelection} of multiple corpus selections.
   *
   * @param corpusNames A List of corpusnames, for which the key is generated.
   * @return A String which is a concatenation of all corpus names, sorted by
   * their names.
   */
  private String buildKey(Set<String> corpusNames)
  {
    SortedSet<String> names = new TreeSet<String>(corpusNames);
    StringBuilder key = new StringBuilder();

    for (String name : names)
    {
      key.append(name);
    }

    return key.toString();
  }
}
