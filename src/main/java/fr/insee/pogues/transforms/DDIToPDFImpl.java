package fr.insee.pogues.transforms;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import fr.insee.eno.GenerationService;
import fr.insee.eno.generation.DDI2PDFGenerator;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.preprocessing.DDIPreprocessor;

@Service
public class DDIToPDFImpl implements DDIToPDF {

	final static Logger logger = LogManager.getLogger(DDIToPDFImpl.class);

	@Override
	public void transform(InputStream input, OutputStream output, Map<String, Object> params, String surveyName) throws Exception {
		logger.debug("Eno transformation");
		if (null == input) {
			throw new NullPointerException("Null input");
		}
		if (null == output) {
			throw new NullPointerException("Null output");
		}
		String odt = transform(input, params,surveyName);
		logger.debug("Eno transformation finished");
		output.write(odt.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String transform(InputStream input, Map<String, Object> params, String surveyName) throws Exception {
		if (null == input) {
			throw new NullPointerException("Null input");
		}
		File enoInput;
		enoInput = File.createTempFile("eno", ".xml");
		FileUtils.copyInputStreamToFile(input, enoInput);
		return transform(enoInput, params,surveyName);
	}

	@Override
	public String transform(String input, Map<String, Object> params, String surveyName) throws Exception {
		File enoInput;
		if (null == input) {
			throw new NullPointerException("Null input");
		}
		enoInput = File.createTempFile("eno", ".xml");
		FileUtils.writeStringToFile(enoInput, input, StandardCharsets.UTF_8);
		return transform(enoInput, params,surveyName);
	}

	private String transform(File file, Map<String, Object> params, String surveyName) throws Exception {

		GenerationService genServiceDDI2PDF = new GenerationService(new DDIPreprocessor(), new DDI2PDFGenerator(),
				new Postprocessor[] {new NoopPostprocessor()});

		File conf = new File(DDIToPDFImpl.class.getResource("/pdf/fop.xconf").toURI());
		InputStream isXconf = new FileInputStream(conf);
		URI imgFolderUri = DDIToPDFImpl.class.getResource("/pdf/img/").toURI();
		
		logger.info("Conf file : "+ conf.getAbsolutePath());
		File outputFO = genServiceDDI2PDF.generateQuestionnaire(file, null,surveyName);
		logger.info("FO output file : "+ outputFO.getAbsolutePath());
		
		
		// Step 1: Construct a FopFactory by specifying a reference to the
		// configuration file
		// (reuse if you plan to render multiple documents!)
		FopFactory fopFactory = FopFactory.newInstance(imgFolderUri,isXconf);

		String outFilePath = FilenameUtils.removeExtension(outputFO.getPath()) + ".pdf";
		File outFilePDF = new File(outFilePath);

		// Step 2: Set up output stream.
		// Note: Using BufferedOutputStream for performance reasons
		// (helpful with FileOutputStreams).
		OutputStream out = new BufferedOutputStream(new FileOutputStream(outFilePDF));

		// Step 3: Construct fop with desired output format
		Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

		// Step 4: Setup JAXP using identity transformer
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer(); // identity
															// transformer

		// Step 5: Setup input and output for XSLT transformation
		// Setup input stream
		Source src = new StreamSource(outputFO);
		// Resulting SAX events (the generated FO) must be piped through
		// to FOP
		Result res = new SAXResult(fop.getDefaultHandler());

		// Step 6: Start XSLT transformation and FOP processing
		transformer.transform(src, res);

		// Clean-up
		out.close();
		
		logger.info("PDF output file : "+ outFilePDF.getAbsolutePath());
		
		return outFilePDF.getAbsolutePath();

	}
}
