package com.badlogic.gdx.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.badlogic.gdx.setup.DependencyBank.ProjectDependency;
import com.badlogic.gdx.setup.DependencyBank.ProjectType;
import com.badlogic.gdx.setup.backend.GenerateProjectParams;

@Service
public class ProjectGeneratorService {
	public static final String GENERATED_VERSION = "1.9.12-SNAPSHOT";
	private ConcurrentHashMap<String, CachedProjects> generatedFiles = new ConcurrentHashMap<>();

	public String generateAndZipGdxProject(GenerateProjectParams projectData) throws Exception {

		DependencyBank bank = new DependencyBank();
		ProjectBuilder builder = new ProjectBuilder(bank);
		List<ProjectType> projects = new ArrayList<ProjectType>();

		projects.add(ProjectType.CORE);
		if (projectData.withDesktop)
			projects.add(ProjectType.DESKTOP);
		if (projectData.withAndroid)
			projects.add(ProjectType.ANDROID);
		if (projectData.withIos)
			projects.add(ProjectType.IOS);
		if (projectData.withHtml)
			projects.add(ProjectType.HTML);

		// TODO current version has template files for 1.9.12-SNAPSHOT, but puts version 1.9.11
		// in it. We have to change that.
		if (!projectData.gdxVersion.equals(GENERATED_VERSION)) {
			projectData.warnings.add("Ignored given gdx-version. Files are generated for 1.9.12-SNAPSHOT.");
		}

		List<Dependency> dependencies = new ArrayList<Dependency>();
		dependencies.add(bank.getDependency(ProjectDependency.GDX));

		Language languageEnum = Language.JAVA;
		builder.buildProject(projects, dependencies);
		builder.build(languageEnum);

		final String uuid = UUID.randomUUID().toString();

		new GdxSetup(new ProjectEmitterZip() {
			@Override
			protected void save() {
				generatedFiles.put(uuid, new CachedProjects(super.byteArrayOutput.toByteArray()));
			}
		}).build(builder, null, projectData.appName, projectData.packageName, projectData.mainClass,
				languageEnum, null, null, null);

		clearCache();

		return uuid;

	}

	@Scheduled(fixedRate = 1000 * 60 * 5, initialDelay = 10000)
	public void clearCache() {
		List<String> uuids = new ArrayList<>(generatedFiles.keySet());

		long timeNow = System.currentTimeMillis();
		long oldestEntryTime = timeNow;
		String oldestEntryUuid = null;

		// remove everything older than 10 minutes
		for (String uuid : uuids) {
			CachedProjects project = generatedFiles.get(uuid);

			if (project != null) {
				if (timeNow - project.timestamp > 1000 * 60 * 10)
					generatedFiles.remove(uuid);
				else if (project.timestamp < oldestEntryTime && timeNow - project.timestamp > 1000 * 60 * 5) {
					oldestEntryUuid = uuid;
					oldestEntryTime = project.timestamp;
				}
			}
		}

		// never more than 5 cached zip files at a time
		if (generatedFiles.size() > 5 && oldestEntryUuid != null) {
			generatedFiles.remove(oldestEntryUuid);
		}
	}

	public CachedProjects getZipFile(String id) {
		return generatedFiles.get(id);
	}

	public static class CachedProjects {
		public final byte[] zippedContent;
		public final long timestamp;

		public CachedProjects(byte[] zipFile) {
			this.zippedContent = zipFile;
			timestamp = System.currentTimeMillis();
		}
	}

}
