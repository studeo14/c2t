package edu.vt.cas2text;

import com.grafresearch.jpdf_parser.uima.annotations.*;
import edu.vt.datasheet_text_processor.Project;
import edu.vt.datasheet_text_processor.Sentence;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.normalizeSpace;

public class CommentTextWriter {
    private final static Logger logger = LoggerFactory.getLogger(CommentTextWriter.class);
    public static void process(JCas jCas, Project project) throws AnalysisEngineProcessException, IOException {
        // get the proper view of the cas
        JCas postJcas;
        try {
            postJcas = jCas.getView("processedText");
        } catch (CASException e) {
            throw new AnalysisEngineProcessException(e);
        }
        // process text
        // go through each sentence collection
        // write sentences (all types)
        for (SentenceCollectionReference sentenceCollectionReference: JCasUtil.select(postJcas, SentenceCollectionReference.class)) {
            // look at type
            if (sentenceCollectionReference instanceof ParagraphReference) {
                for (SentenceReference sr: JCasUtil.selectCovered(SentenceReference.class, sentenceCollectionReference)) {
                    // do header stuff
                    logger.debug("{}::{}", sr.getBegin(), sr.getCoveredText());
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
                logger.debug("Table Start: {}::{}", tr.getBegin(), captionText);
                addSentence(String.format("Table Start: \"%s\"", captionText), tr.getBegin(), 3, Sentence.Type.META, project);
                for (SentenceReference sr: JCasUtil.selectCovered(SentenceReference.class, sentenceCollectionReference)) {
                    // do header stuff
                    logger.debug("{}::{}", sr.getBegin(), sr.getCoveredText());
                    // write sentence
                    writer.write(String.format("%d;4;N;%s\n", sr.getBegin(), sr.getCoveredText()));
                }
                logger.debug("Table End: {}", tr.getEnd());
                addSentence(String.format("Table End: \"%s\"", captionText), tr.getEnd(), 0, Sentence.Type.META, project);
            } else if (sentenceCollectionReference instanceof ListReference) {
                ListReference lr = (ListReference) sentenceCollectionReference;
                logger.debug("List Start: {}", lr.getBegin());
                addSentence("List Start", lr.getBegin(), 3, Sentence.Type.META, project);
                for (ListItemReference ir: JCasUtil.selectCovered(ListItemReference.class, sentenceCollectionReference)) {
                    // TODO List Item start/end?
                    // do sentences
                    for (ListSentenceReference sr: JCasUtil.selectCovered(ListSentenceReference.class, ir)) {
                        // do header stuff
                        logger.debug("{}::{}", sr.getBegin(), sr.getCoveredText());
                        // write sentence
                        writer.write(String.format("%d;4;N;%s\n", sr.getBegin(), sr.getCoveredText()));
                    }
                    // do sublists start/end
                    for (SubListReference sublist: JCasUtil.selectCovered(SubListReference.class, ir)) {
                        logger.debug("List Start: {}", sublist.getBegin());
                        addSentence("List Start", sublist.getBegin(), 3, Sentence.Type.META, project);
                        logger.debug("List End: {}", sublist.getEnd());
                        addSentence("List End", lr.getEnd(), 0, Sentence.Type.META, project);
                    }
                }
                logger.debug("List End: {}", lr.getEnd());
                addSentence("List End", lr.getEnd(), 0, Sentence.Type.META, project);
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
            var headerText = normalizeSpace(currentHeader.getCoveredText());
            logger.debug("Section Start: {}::{}", sectionReference.getBegin(), headerText);
            addSentence(String.format("Section Start: \"%s\"", headerText), sectionReference.getBegin(), 2, Sentence.Type.META, project);
            logger.debug("Section End: {}::{}", sectionReference.getEnd(), headerText);
            addSentence(String.format("Section End: \"%s\"", headerText), sectionReference.getEnd(), 1, Sentence.Type.META, project);
        }
        writer.close();
    }
}

