package com.example.demo;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;

@SpringBootApplication
public class DemoApplication {
	private static final String FILENAME = "D:\\Temp\\ExportDir\\NonExistingFile.txt";
	private final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired private Environment env;

	public static void main(String[] args) {
		System.setProperty("com.example.demo.data.file", FILENAME);
		SpringApplication.run(DemoApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			// When running with 'java -jar demo-0.0.1-SNAPSHOT.jar', this prints
			// org.springframework.boot.loader.LaunchedURLClassLoader@475530b9
			log.info("Classloader: "+this.getClass().getClassLoader());
			
			try {
				// This is more or less an example of what is failing in my application
				log.info("Data: "+new Binder(ConfigurationPropertySources.get(env)).bind("com.example.demo.data", Data.class).get());
			} catch ( RuntimeException e ) {
				log.error("Error binding Data class", e);
			}
			
			try {
				// This demonstrates the failing call to 'resource.exists()' in 
				// org.springframework.boot.convert.StringToFileConverter#convert(String), 
				// causing the conversion to fail
				log.info("Resource exists: "+new DefaultResourceLoader(null).getResource(FILENAME).exists());
			} catch ( RuntimeException e ) {
				log.error("Error checking resource exists", e);
			}	
		};
	}
	
	public static final class Data {
		private File file;

		public File getFile() {
			return file;
		}

		public void setFile(File file) {
			this.file = file;
		}
		
		@Override
		public String toString() {
			return String.format("Data[file=%s]", file);
		}
	}

}
