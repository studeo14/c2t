package edu.vt.cas2text;

import com.grafresearch.jpdf_parser.uima.annotations.ReferenceAnnotation;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommentTextWriter {
    private final static Logger logger = LogManager.getLogger(CommentTextWriter.class);
    public static void process(JCas jCas, File output, boolean includeHeaders) throws AnalysisEngineProcessException, IOException {
        JCas postJcas;
        try {
            postJcas = jCas.getView("processedText");
        } catch (CASException e) {
            throw new AnalysisEngineProcessException(e);
        }
        var outStream = new FileOutputStream(output);
        var writer = new BufferedWriter(new OutputStreamWriter(outStream));
        // process text
        for (ReferenceAnnotation ref : JCasUtil.select(postJcas, ReferenceAnnotation.class)) {
            Pair<Integer, String> sentence = Pair.of(ref.getBegin(), ref.getCoveredText());
            writer.write(String.format("%d::%s", sentence.getLeft(), sentence.getRight()));
        }
    }
}

