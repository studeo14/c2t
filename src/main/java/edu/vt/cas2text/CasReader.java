package edu.vt.cas2text;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.factory.TypePrioritiesFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.CasIOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CasReader {
    private static final Logger logger = LogManager.getLogger(CasReader.class);

    public static void readCas(File input, File output, boolean header) throws IOException {
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
            // run through the annotator
            var jcas = cas.getJCas();
            CommentTextWriter.process(jcas, output, header);
        } catch (ResourceInitializationException | AnalysisEngineProcessException | CASException e) {
            logger.fatal(e);
        }
    }
}
