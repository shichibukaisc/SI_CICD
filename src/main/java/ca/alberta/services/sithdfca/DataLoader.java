package ca.alberta.services.sithdfca;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.alberta.services.sithdfca.model.SubscriptionInfo;
import ca.alberta.services.sithdfca.repositories.SubscriptionInfoJpaRepository;
import lombok.extern.slf4j.Slf4j;


/**
 * The DataLoader will load all subscription client information into the SubscriptionInfo repository.  This is needed
 * currently to ensure that confidential subscription metadata is able to be stored in secure places such Credential vaults.
 * 
 * @author jj_es
 *
 */
@Slf4j
@Component
public class DataLoader implements CommandLineRunner {


	@Autowired
	private SubscriptionInfoJpaRepository siRepo;

	@Value("${application.clientId.dataLoader.folderPath}")
	private String autoLoaderPath;


	@Override
	public void run(String... args) throws Exception {
		log.debug("In DataLoader.load()");
		final File folder = new File(this.autoLoaderPath);
		try {
			readFilesForFolder(folder);
		}catch(IOException ioe) {
			log.error(ioe.getMessage());
		}catch(Exception e) {
			log.error(e.getMessage());
		}		
	}
	
	/**
	 * Loop through all the files given a path
	 * @param folder
	 * @throws IOException
	 */
	private void readFilesForFolder(final File folder) throws  IOException {
		log.error("This is the loader path: "+ this.autoLoaderPath);
		//Only walking the depth of the autoLoaderPath folder 
		try (Stream<Path> paths = Files.walk(Paths.get(this.autoLoaderPath),1)) {
		    paths
		        //.filter(path -> Files.isRegularFile(path,LinkOption.NOFOLLOW_LINKS))
		    	.filter(Files::isRegularFile)
		        .forEach(path -> loadRepo(path));
		} 
	}
	
	/**
	 * Check for a specific clientId (file) The files named in the autoLoaderPath reflect the clientId of the file
	 * @param folder
	 * @throws IOException
	 */
	public void checkForFile(String fileName) throws  IOException {
		log.debug("This is the loader path: "+ this.autoLoaderPath);
		log.debug("This is the filename we are looking for in the folder: " + fileName);
		Path p = Paths.get(this.autoLoaderPath + "/" + fileName);
		if(p != null && Files.isRegularFile(p)) {
			log.debug("File " + p.getFileName() + " exists.  Loading file into SubscriptionInfo repo");
			loadRepo(p);
		}
	}
	
	/**
	 * Load the contents of a file into the SubscriptionInfo repository.
	 * The file is expected to be a properly formatted json that has the structure of a valid SubscriptionInfo object.
	 * @param p
	 */
	
	private void loadRepo(Path p){
		ObjectMapper om = new ObjectMapper();
		log.debug("calling loadRepo with the following path (toString): " + p.toString());
		log.debug("calling loadRepo with the following path (getFileName): " + p.getFileName());
		try {
			if(p != null) {
				//
				String json = Files.readString(p);
				try {
		    		SubscriptionInfo si = om.readValue(json, SubscriptionInfo.class);
		    		si.getSubscriptionInfoValues().stream().forEach(siv -> siv.setSubscriptionInfo(si));
		    		siRepo.save(si);
		    	}catch (Exception e) {
		    		log.error(e.getMessage());
				}
			}
		}catch (IOException e) {
			log.error(e.getMessage());
			// TODO: handle exception
		}
	}
}