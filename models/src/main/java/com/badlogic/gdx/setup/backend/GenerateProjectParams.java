package com.badlogic.gdx.setup.backend;

import java.util.LinkedList;
import java.util.List;

public class GenerateProjectParams {
	public String gdxVersion = "1.9.12";
	public String appName;
	public String packageName;
	public String mainClass;
	public boolean withAndroid;
	public boolean withIos;
	public boolean withHtml;
	public boolean withDesktop;

	public List<String> warnings = new LinkedList<>();

	public String getGdxVersion() {
		return gdxVersion;
	}

	public void setGdxVersion(String gdxVersion) {
		this.gdxVersion = gdxVersion;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getMainClass() {
		return mainClass;
	}

	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}

	public boolean isWithAndroid() {
		return withAndroid;
	}

	public void setWithAndroid(boolean withAndroid) {
		this.withAndroid = withAndroid;
	}

	public boolean isWithIos() {
		return withIos;
	}

	public void setWithIos(boolean withIos) {
		this.withIos = withIos;
	}

	public boolean isWithHtml() {
		return withHtml;
	}

	public void setWithHtml(boolean withHtml) {
		this.withHtml = withHtml;
	}

	public boolean isWithDesktop() {
		return withDesktop;
	}

	public void setWithDesktop(boolean withDesktop) {
		this.withDesktop = withDesktop;
	}

	public List<String> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}
}
