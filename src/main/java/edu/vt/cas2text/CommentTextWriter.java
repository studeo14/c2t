package edu.vt.cas2text;

import com.grafresearch.jpdf_parser.uima.annotations.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

import java.io.*;
import java.util.List;

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
        // go through each sentence collection
        Header lastHeader = null;
        // write sentences (all types)
        for (SentenceCollectionReference sentenceCollectionReference: JCasUtil.select(postJcas, SentenceCollectionReference.class)) {
            // look at type
            if (sentenceCollectionReference instanceof ParagraphReference) {
                for (SentenceReference sr: JCasUtil.selectCovered(SentenceReference.class, sentenceCollectionReference)) {
                    // do header stuff
                    logger.info("{}::{}", sr.getBegin(), sr.getCoveredText());
                    // write sentence
                    writer.write(String.format("%d::%s\n", sr.getBegin(), sr.getCoveredText()));
                }
            } else if (sentenceCollectionReference instanceof TableReference) {
                TableReference tr = (TableReference) sentenceCollectionReference;
                var caption = tr.getReference().getCaption();
                logger.info("Table Start: {}", tr.getBegin());
                writer.write(String.format("%d::Table Start: \"%s\"\n", tr.getBegin(), caption));
                int lastEnd = tr.getBegin();
                for (SentenceReference sr: JCasUtil.selectCovered(SentenceReference.class, sentenceCollectionReference)) {
                    // do header stuff
                    logger.info("{}::{}", sr.getBegin(), sr.getCoveredText());
                    // write sentence
                    writer.write(String.format("%d::%s\n", sr.getBegin(), sr.getCoveredText()));
                    if (sr.getEnd() > lastEnd) {
                        lastEnd = sr.getEnd();
                    }
                }
                logger.info("Table End: {}", lastEnd);
                writer.write(String.format("%d::Table End: \"%s\"\n", lastEnd, caption));
            } else if (sentenceCollectionReference instanceof ListReference) {
                ListReference lr = (ListReference) sentenceCollectionReference;
                logger.info("List Start: {}", lr.getBegin());
                writer.write(String.format("%d::List Start\n", lr.getBegin()));
                int lastEnd = lr.getBegin();
                for (SentenceReference sr: JCasUtil.selectCovered(SentenceReference.class, sentenceCollectionReference)) {
                    // do header stuff
                    logger.info("{}::{}", sr.getBegin(), sr.getCoveredText());
                    // write sentence
                    writer.write(String.format("%d::%s\n", sr.getBegin(), sr.getCoveredText()));
                    if (sr.getEnd() > lastEnd) {
                        lastEnd = sr.getEnd();
                    }
                }
                logger.info("List End: {}", lastEnd);
                writer.write(String.format("%d::List End\n", lastEnd));
            } else if (sentenceCollectionReference instanceof FigureReference) {
                // TODO: do this?
            }
        }
        // write section headers
        for (SectionReference sectionReference : JCasUtil.select(postJcas, SectionReference.class)) {
            // handle section/header stuff
            Header currentHeader = sectionReference.getReference();
            // check if a new section
            if (currentHeader != lastHeader) {
                if (lastHeader != null) {
                    logger.info("Section End: {}::{}", lastHeader.getEnd(), lastHeader.getCoveredText());
                    writer.write(String.format("%d::Section End: \"%s\"\n", lastHeader.getEnd(), lastHeader.getCoveredText()));
                }
                // change header
                lastHeader = currentHeader;
                // add a sentence
                logger.info("Section Start: {}::{}", lastHeader.getBegin(), lastHeader.getCoveredText());
                writer.write(String.format("%d::Section start: \"%s\"\n", lastHeader.getBegin(), lastHeader.getCoveredText()));
            }
        }
        writer.close();
    }
}

