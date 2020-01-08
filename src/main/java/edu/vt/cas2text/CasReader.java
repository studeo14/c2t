package edu.vt.cas2text;

import edu.vt.datasheet_text_processor.ProjectUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.factory.TypePrioritiesFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.CasIOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CasReader {
    private static final Logger logger = LoggerFactory.getLogger(CasReader.class);

    public static void readCas(File input) throws IOException {
        //
        try {
            // use type auto-detection
            // see: https://uima.apache.org/d/uimafit-current/tools.uimafit.book.html#d5e566 (chapter 8.)
            // reads from type system xml files (listed in META-INF/org.apache.uima.fit/types.txt
            var tsd = TypeSystemDescriptionFactory.createTypeSystemDescription();
            // most likely blank
            var tsp = TypePrioritiesFactory.createTypePriorities();
            // create a blank cas
            var cas = CasCreationUtils.createCas(tsd, tsp, null);
            // read in
            CasIOUtils.load(new FileInputStream(input), cas);
            // create project file to use
            var project = ProjectUtils.createEmptyProject(input);
            // run through the annotator
            var jcas = cas.getJCas();
            CommentTextWriter.process(jcas, project);
            logger.info("Wrote project file to {}.project", project.getName());
            // write out
            project.getDB().commit();
            project.getDB().close();
        } catch (ResourceInitializationException | AnalysisEngineProcessException | CASException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
