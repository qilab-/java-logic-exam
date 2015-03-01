package jp.qilab.jle.javac;

import java.io.IOException;
import java.security.SecureClassLoader;
import java.util.*;

import javax.tools.*;
import javax.tools.JavaFileObject.Kind;

public class ClassFileManager extends ForwardingJavaFileManager<JavaFileManager> {

	public ClassFileManager(JavaCompiler compiler, DiagnosticListener<? super JavaFileObject> listener) {
		super(compiler.getStandardFileManager(listener, null, null));
	}

	/** キー：クラス名、値：クラスファイルのオブジェクト */
	protected final Map<String, JavaClassObject> map = new HashMap<String, JavaClassObject>();

	// クラスファイルを生成するときに呼ばれる
	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
		JavaClassObject co = new JavaClassObject(className, kind);
		map.put(className, co); // クラス名をキーにしてファイルオブジェクトを保持しておく
		return co;
	}

	protected ClassLoader loader = null;

	@Override
	public ClassLoader getClassLoader(Location location) {
		if (loader == null) {
			loader = new Loader();
		}
		return loader;
	}

	/** コンパイルしたクラスを返す為のクラスローダー */
	private class Loader extends SecureClassLoader {

		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			JavaClassObject co = map.get(name);
			if (co == null) {
				return super.findClass(name);
			}

			Class<?> c = co.getDefinedClass();
			if (c == null) {
				byte[] b = co.getBytes();
				c = super.defineClass(name, b, 0, b.length);
				co.setDefinedClass(c);
			}
			return c;
		}
	}
}
