package jp.qilab.jle.javac;

import java.io.*;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class JavaClassObject extends SimpleJavaFileObject {

	public JavaClassObject(String name, Kind kind) {
		super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
	}

	protected final ByteArrayOutputStream bos = new ByteArrayOutputStream();

	// 実際の出力用ストリームを返す
	@Override
	public OutputStream openOutputStream() throws IOException {
		return bos;
	}

	// コンパイルされたバイトコード
	public byte[] getBytes() {
		return bos.toByteArray();
	}

	/** ロードされたクラス */
	private Class<?> clazz = null;

	public void setDefinedClass(Class<?> c) {
		clazz = c;
	}

	public Class<?> getDefinedClass() {
		return clazz;
	}
}
