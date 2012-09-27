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
package annis.gui.widgets;

import annis.gui.MatchedNodeColors;
import annis.gui.visualizers.component.grid.GridEvent;
import annis.gui.visualizers.component.grid.Row;
import annis.gui.widgets.gwt.client.VAnnotationGrid;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.ClientWidget;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Thomas Krause <thomas.krause@alumni.hu-berlin.de>
 */
@ClientWidget(VAnnotationGrid.class)
public class AnnotationGrid extends AbstractComponent
{

  private Map<String, ArrayList<Row>> rowsByAnnotation;

  public AnnotationGrid()
  {
  }

  @Override
  public void paintContent(PaintTarget target) throws PaintException
  {
    super.paintContent(target);

    if (rowsByAnnotation != null)
    {
      target.startTag("rows");
      for (Map.Entry<String, ArrayList<Row>> anno : rowsByAnnotation.entrySet())
      {

        for (Row row : anno.getValue())
        {
          target.startTag("row");
          target.addAttribute("caption", anno.getKey());

          target.startTag("events");
          for (GridEvent event : row.getEvents())
          {
            target.startTag("event");
            target.addAttribute("id", event.getId());
            target.addAttribute("left", event.getLeft());
            target.addAttribute("right", event.getRight());
            target.addAttribute("value", event.getValue());

            ArrayList<String> styles = getStyles(event, anno.getKey());
            if (styles.size() > 0)
            {
              target.addAttribute("style", styles.toArray());
            }

            // define a list of covered token that are hightlighted whenever this
            // event is hovered
            target.addAttribute("highlight", event.getCoveredIDs().toArray());

            target.endTag("event");
          }
          target.endTag("events");

          target.endTag("row");
        }
      }
      target.endTag("rows");
    }

    target.addAttribute("test", 100);

  }

  private ArrayList<String> getStyles(GridEvent event, String annoName)
  {
    ArrayList<String> styles = new ArrayList<String>();

    if ("tok".equals(annoName))
    {
      styles.add("token");
    }
    else
    {
      styles.add("single_event");
    }
    
    if (event.getMatch() != null)
    {
      // re-use the style from KWIC, which means we have to add a vaadin-specific
      // prefix
      styles.add("v-table-cell-content-" 
        + MatchedNodeColors.colorClassByMatch(event.getMatch()));
    }


    return styles;
  }

  public Map<String, ArrayList<Row>> getRowsByAnnotation()
  {
    return rowsByAnnotation;
  }

  public void setRowsByAnnotation(Map<String, ArrayList<Row>> rowsByAnnotation)
  {
    this.rowsByAnnotation = rowsByAnnotation;
  }
}
