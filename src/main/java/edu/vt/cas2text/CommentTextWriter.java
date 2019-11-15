package edu.vt.cas2text;

import com.grafresearch.jpdf_parser.uima.annotations.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

import java.io.*;
import java.util.List;

public class CommentTextWriter {
    private final static Logger logger = LogManager.getLogger(CommentTextWriter.class);
    public static void process(JCas jCas, File output) throws AnalysisEngineProcessException, IOException {
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
        // write sentences (all types)
        for (SentenceCollectionReference sentenceCollectionReference: JCasUtil.select(postJcas, SentenceCollectionReference.class)) {
            // look at type
            if (sentenceCollectionReference instanceof ParagraphReference) {
                for (SentenceReference sr: JCasUtil.selectCovered(SentenceReference.class, sentenceCollectionReference)) {
                    // do header stuff
                    logger.info("{}::{}", sr.getBegin(), sr.getCoveredText());
                    // write sentence
                    writer.write(String.format("%d;4;N;%s\n", sr.getBegin(), sr.getCoveredText()));
                }
            } else if (sentenceCollectionReference instanceof TableReference) {
                TableReference tr = (TableReference) sentenceCollectionReference;
                var caption = tr.getReference().getCaption();
                var captionText = "";
                if (caption != null) {
                    captionText = caption.getCoveredText();
                }
                logger.info("Table Start: {}::{}", tr.getBegin(), captionText);
                writer.write(String.format("%d;3;M;Table Start: \"%s\"\n", tr.getBegin(), captionText));
                for (SentenceReference sr: JCasUtil.selectCovered(SentenceReference.class, sentenceCollectionReference)) {
                    // do header stuff
                    logger.info("{}::{}", sr.getBegin(), sr.getCoveredText());
                    // write sentence
                    writer.write(String.format("%d;4;N;%s\n", sr.getBegin(), sr.getCoveredText()));
                }
                logger.info("Table End: {}", tr.getEnd());
                writer.write(String.format("%d;0;M;Table End: \"%s\"\n", tr.getEnd(), captionText));
            } else if (sentenceCollectionReference instanceof ListReference) {
                ListReference lr = (ListReference) sentenceCollectionReference;
                logger.info("List Start: {}", lr.getBegin());
                writer.write(String.format("%d;3;M;List Start\n", lr.getBegin()));
                for (ListItemReference ir: JCasUtil.selectCovered(ListItemReference.class, sentenceCollectionReference)) {
                    // TODO List Item start/end?
                    // do sentences
                    for (ListSentenceReference sr: JCasUtil.selectCovered(ListSentenceReference.class, ir)) {
                        // do header stuff
                        logger.info("{}::{}", sr.getBegin(), sr.getCoveredText());
                        // write sentence
                        writer.write(String.format("%d;4;N;%s\n", sr.getBegin(), sr.getCoveredText()));
                    }
                    // do sublists start/end
                    for (SubListReference sublist: JCasUtil.selectCovered(SubListReference.class, ir)) {
                        logger.info("List Start: {}", sublist.getBegin());
                        writer.write(String.format("%d;3;M;List Start\n", sublist.getBegin()));
                        logger.info("List End: {}", sublist.getEnd());
                        writer.write(String.format("%d;0;M;List End\n", sublist.getEnd()));
                    }
                }
                logger.info("List End: {}", lr.getEnd());
                writer.write(String.format("%d;0;M;List End\n", lr.getEnd()));
            } else if (sentenceCollectionReference instanceof FigureReference) {
                // TODO: do this?
            }
        }
        Header lastHeader = null;
        SectionReference lastSection = null;
        // write section headers
        for (SectionReference sectionReference : JCasUtil.select(postJcas, SectionReference.class)) {
            // handle section/header stuff
            Header currentHeader = sectionReference.getReference();
            var headerText = currentHeader.getCoveredText();
            logger.info("Section Start: {}::{}", sectionReference.getBegin(), headerText);
            writer.write(String.format("%d;2;M;Section start: \"%s\"\n", sectionReference.getBegin(), headerText));
            logger.info("Section End: {}::{}", sectionReference.getEnd(), headerText);
            writer.write(String.format("%d;1;M;Section End: \"%s\"\n", sectionReference.getEnd(), headerText));
        }
        writer.close();
    }
}

