package edu.vt.cas2text;

import edu.vt.datasheet_text_processor.ProjectUtils;
import edu.vt.datasheet_text_processor.Sentence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TextReader {
    private final static Logger logger = LoggerFactory.getLogger(TextReader.class);

    public static void readText(File input) throws IOException {
        // create a project
        var project = ProjectUtils.createEmptyProject(input);
        var db = project.getDB();

        var repo = db.getRepository(Sentence.class);
        logger.info("Created sentence repository. In {}.project.", project.getName());

        var id = 1;
        for (String line : Files.readAllLines(input.toPath())) {
            if (line.isEmpty() || line.isBlank()) {
                continue;
            }
            var newSentence = new Sentence(id, 4, line);
            // increment
            repo.insert(newSentence);
            logger.debug("Added '{}' as NA sentence with ID: {}.", line, id);
            id++;
        }

        logger.info("Raw Sentences added.");

        if (db.hasUnsavedChanges()) {
            db.commit();
            logger.debug("{}.project saved.", project.getName());
        }
        db.close();
    }
}
