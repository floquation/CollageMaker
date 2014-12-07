package framework.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import framework.FileIO;

public class PCFileIO implements FileIO {
    String externalStoragePath;
    String assetsPath;

    public PCFileIO(String externalStoragePath, String assetsPath) {
    	this.assetsPath = assetsPath + "/";
        this.externalStoragePath = externalStoragePath + "/";
    }

    public InputStream readAsset(String fileName) throws IOException {
        return new FileInputStream(assetsPath + fileName);
    }

    public InputStream readFile(String fileName) throws IOException {
        return new FileInputStream(externalStoragePath + fileName);
    }

    public OutputStream writeFile(String fileName) throws IOException {
        return new FileOutputStream(externalStoragePath + fileName);
    }

	@Override
	public InputStream readFile(File file) throws IOException {
        return new FileInputStream(file);
	}

	@Override
	public OutputStream writeFile(File file) throws IOException {
        return new FileOutputStream(file);
	}
    
}
