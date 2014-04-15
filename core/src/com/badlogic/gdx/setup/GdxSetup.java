/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.setup;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.setup.DependencyBank.ProjectDependency;
import com.badlogic.gdx.setup.DependencyBank.ProjectType;
import com.badlogic.gdx.setup.Executor.CharCallback;

/**
 * Command line tool to generate libgdx projects
 * @author badlogic
 *
 */
public class GdxSetup {
	public static boolean isSdkLocationValid (String sdkLocation) {
		return new File(sdkLocation, "tools").exists() && new File(sdkLocation, "platforms").exists();
	}
	
	public void build (ProjectBuilder builder, String outputDir, String appName, String packageName, String mainClass, String sdkLocation, CharCallback callback) {
		Project project = new Project();

		boolean ui = builder != null;

		String packageDir = packageName.replace('.', '/');
		String sdkPath = sdkLocation.replace('\\', '/');

		String assetPath;
		if (ui) {
			assetPath = builder.modules.contains(ProjectType.ANDROID) ? "android/assets" : "core/assets";
		} else {
			assetPath = "android/assets";
		}

		if(!isSdkLocationValid(sdkLocation)) {
			System.out.println("Android SDK location '" + sdkLocation + "' doesn't contain an SDK");
		}

		// root dir/gradle files
		project.files.add(new ProjectFile("gitignore", ".gitignore", false));
		// Use our temp build and settings files if we are from the ui
		if (ui) {
			project.files.add(new TemporaryProjectFile(builder.settingsFile, "settings.gradle", false));
			project.files.add(new TemporaryProjectFile(builder.buildFile, "build.gradle", true));
		} else {
			project.files.add(new ProjectFile("build.gradle", true));
			project.files.add(new ProjectFile("settings.gradle", false));
		}
		project.files.add(new ProjectFile("gradlew", false));
		project.files.add(new ProjectFile("gradlew.bat", false));
		project.files.add(new ProjectFile("gradle/wrapper/gradle-wrapper.jar", false));
		project.files.add(new ProjectFile("gradle/wrapper/gradle-wrapper.properties", false));
		project.files.add(new ProjectFile("local.properties", true));

		// core project
		project.files.add(new ProjectFile("core/build.gradle"));
		project.files.add(new ProjectFile("core/src/MainClass", "core/src/" + packageDir + "/" + mainClass + ".java", true));
		//core but html required
		project.files.add(new ProjectFile("core/CoreGdxDefinition", "core/src/" + packageDir + "/" + mainClass + ".gwt.xml", true));
		
		// desktop project
		if (!ui || builder.modules.contains(ProjectType.DESKTOP)) {
			project.files.add(new ProjectFile("desktop/build.gradle"));
			project.files.add(new ProjectFile("desktop/src/DesktopLauncher", "desktop/src/" + packageDir + "/desktop/DesktopLauncher.java", true));
		}

		//Assets
		project.files.add(new ProjectFile("android/assets/badlogic.jpg", assetPath + "/badlogic.jpg", false));

		// android project
		if (!ui || builder.modules.contains(ProjectType.ANDROID)) {
			project.files.add(new ProjectFile("android/res/values/strings.xml"));
			project.files.add(new ProjectFile("android/res/values/styles.xml", false));
			project.files.add(new ProjectFile("android/res/drawable-hdpi/ic_launcher.png", false));
			project.files.add(new ProjectFile("android/res/drawable-mdpi/ic_launcher.png", false));
			project.files.add(new ProjectFile("android/res/drawable-xhdpi/ic_launcher.png", false));
			project.files.add(new ProjectFile("android/res/drawable-xxhdpi/ic_launcher.png", false));
			project.files.add(new ProjectFile("android/src/AndroidLauncher", "android/src/" + packageDir + "/android/AndroidLauncher.java", true));
			project.files.add(new ProjectFile("android/AndroidManifest.xml"));
			project.files.add(new ProjectFile("android/build.gradle", true));
			project.files.add(new ProjectFile("android/ic_launcher-web.png", false));
			project.files.add(new ProjectFile("android/proguard-project.txt", false));
			project.files.add(new ProjectFile("android/project.properties", false));
		}

		//html project
		if (!ui || builder.modules.contains(ProjectType.HTML)) {
			project.files.add(new ProjectFile("html/build.gradle"));
			project.files.add(new ProjectFile("html/src/HtmlLauncher", "html/src/" + packageDir + "/client/HtmlLauncher.java", true));
			project.files.add(new ProjectFile("html/GdxDefinition", "html/src/" + packageDir + "/GdxDefinition.gwt.xml", true));
			project.files.add(new ProjectFile("html/GdxDefinitionSuperdev", "html/src/" + packageDir + "/GdxDefinitionSuperdev.gwt.xml", true));
			project.files.add(new ProjectFile("html/war/index", "html/webapp/index.html", true));
			project.files.add(new ProjectFile("html/war/styles.css", "html/webapp/styles.css", false));
			project.files.add(new ProjectFile("html/war/soundmanager2-jsmin.js", "html/webapp/soundmanager2-jsmin.js", false));
			project.files.add(new ProjectFile("html/war/soundmanager2-setup.js", "html/webapp/soundmanager2-setup.js", false));
			project.files.add(new ProjectFile("html/war/WEB-INF/web.xml", "html/webapp/WEB-INF/web.xml", true));
		}

		//ios robovm
		if (!ui || builder.modules.contains(ProjectType.IOS)) {
			project.files.add(new ProjectFile("ios/src/IOSLauncher", "ios/src/" + packageDir + "/IOSLauncher.java", true));
			project.files.add(new ProjectFile("ios/build.gradle", true));
			project.files.add(new ProjectFile("ios/Info.plist.xml", false));
			project.files.add(new ProjectFile("ios/robovm.properties"));
			project.files.add(new ProjectFile("ios/robovm.xml", true));
		}

		Map<String, String> values = new HashMap<String, String>();
		values.put("%APP_NAME%", appName);
		values.put("%PACKAGE%", packageName);
		values.put("%MAIN_CLASS%", mainClass);
		values.put("%ANDROID_SDK%", sdkPath);
		values.put("%ASSET_PATH%", assetPath);
		if (!ui || builder.modules.contains(ProjectType.HTML)) {
			values.put("%GWT_INHERITS%", parseGwtInherits(builder.bank.gwtInheritances));
		}
		
		copyAndReplace(outputDir, project, values);

		builder.cleanUp();
		
		// HACK executable flag isn't preserved for whatever reason...
		new File(outputDir, "gradlew").setExecutable(true);
		
		Executor.execute(new File(outputDir), "gradlew.bat", "gradlew", "clean", callback);
	}

	private void copyAndReplace (String outputDir, Project project, Map<String, String> values) {
		File out = new File(outputDir);
		if(!out.exists() && !out.mkdirs()) {
			throw new RuntimeException("Couldn't create output directory '" + out.getAbsolutePath() + "'");
		}
		
		for(ProjectFile file: project.files) {
			copyFile(file, out, values);
		}
	}
	
	private byte[] readResource(String resource, String path) {
		InputStream in = null;
		try {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024*10];
			in = GdxSetup.class.getResourceAsStream(path + resource);
			if(in == null) throw new RuntimeException("Couldn't read resource '" + resource + "'");
			int read = 0;
			while((read = in.read(buffer)) > 0) {
				bytes.write(buffer, 0, read);
			}
			return bytes.toByteArray();
		} catch(IOException e) {
			throw new RuntimeException("Couldn't read resource '" + resource + "'", e);
		} finally {
			if(in != null) try { in.close(); } catch(IOException e) {}
		}
	}

	private byte[] readResource(File file) {
		InputStream in = null;
		try {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024*10];
			in = new FileInputStream(file);
			if (in == null) throw new RuntimeException("Couldn't read resource '" + file.getAbsoluteFile() + "'");
			int read = 0;
			while ((read = in.read(buffer)) > 0) {
				bytes.write(buffer, 0, read);
			}
			return bytes.toByteArray();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Couldn't read resource '" + file.getAbsoluteFile() + "'", e);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't read resource '" + file.getAbsoluteFile() + "'", e);
		} finally {
			if(in != null) try { in.close(); } catch(IOException e) {}
		}
	}
	
	private String readResourceAsString(String resource, String path) {
		try {
			return new String(readResource(resource, path), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private String readResourceAsString(File file) {
		try {
			return new String(readResource(file), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void writeFile (File outFile, byte[] bytes) {
		OutputStream out = null;
		
		try {
			out = new BufferedOutputStream(new FileOutputStream(outFile));
			out.write(bytes);
		} catch(IOException e) {
			throw new RuntimeException("Couldn't write file '" + outFile.getAbsolutePath() + "'", e);
		} finally {
			if(out != null) try { out.close(); } catch(IOException e) {}
		}
	}
	
	private void writeFile(File outFile, String text) {
		try {
			writeFile(outFile, text.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private void copyFile(ProjectFile file, File out, Map<String, String> values) {
		File outFile = new File(out, file.outputName);
		if(!outFile.getParentFile().exists() && !outFile.getParentFile().mkdirs()) {
			throw new RuntimeException("Couldn't create dir '" + outFile.getAbsolutePath() + "'");
		}

		boolean isTemp = file instanceof TemporaryProjectFile ? true : false;
		
		if(file.isTemplate) {
			String txt;
			if (isTemp) {
				txt = readResourceAsString(((TemporaryProjectFile) file).file);
			} else {
				txt = readResourceAsString(file.resourceName, file.resourceLoc);
			}
			txt = replace(txt, values);
			writeFile(outFile, txt);
		} else {
			if (isTemp) {
				writeFile(outFile, readResource(((TemporaryProjectFile) file).file));
			} else {
				writeFile(outFile, readResource(file.resourceName, file.resourceLoc));
			}
		}
	}

	private String replace (String txt, Map<String, String> values) {
		for(String key: values.keySet()) {
			String value = values.get(key);
			txt = txt.replace(key, value);
		}
		return txt;
	}
	
	private static void printHelp() {
		System.out.println("Usage: GdxSetup --dir <dir-name> --name <app-name> --package <package> --mainClass <mainClass> --sdkLocation <SDKLocation>");
		System.out.println("dir ... the directory to write the project files to");
		System.out.println("name ... the name of the application");
		System.out.println("package ... the Java package name of the application");
		System.out.println("mainClass ... the name of your main ApplicationListener");
		System.out.println("sdkLocation ... the location of your android SDK. Uses ANDROID_HOME if not specified");
	}
	
	private static Map<String, String> parseArgs(String[] args) {
		if(args.length % 2 != 0) {
			printHelp();
			System.exit(-1);
		}
		
		Map<String, String> params = new HashMap<String, String>();
		for(int i = 0; i < args.length; i+=2) {
			String param = args[i].replace("--", "");
			String value = args[i+1];
			params.put(param, value);
		}
		return params;
	}

	private String parseGwtInherits(HashMap<ProjectDependency, String[]> gwtInheritances) {
		String parsed = "";
		for (String[] array : gwtInheritances.values()) {
			for (String inherit : array) {
				parsed += "\t<inherits name='" + inherit + "' />\n";
			}
		}
		return parsed;
	}
	
	public static void main (String[] args) {
		Map<String, String> params = parseArgs(args);
                if(!params.containsKey("dir") || !params.containsKey("name") || !params.containsKey("package") || !params.containsKey("mainClass") || ((!params.containsKey("sdkLocation") && System.getenv("ANDROID_HOME") == null))) {
			new GdxSetupUI();
			printHelp();
		} else {
			String sdkLocation = "";
			if (System.getenv("ANDROID_HOME") != null && !params.containsKey("sdkLocation")) {
				sdkLocation = System.getenv("ANDROID_HOME");
			} else {
				sdkLocation = params.get("sdkLocation");
			}
			new GdxSetup().build(null, params.get("dir"), params.get("name"), params.get("package"), params.get("mainClass"), sdkLocation, new CharCallback() {
				@Override
				public void character (char c) {
					System.out.print(c);
				}
			});
		}
	}
}