package jp.qilab.jle;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import jp.qilab.jle.javac.ClassFileManager;
import jp.qilab.jle.javac.ErrorListener;
import jp.qilab.jle.javac.JavaSourceFromString;
import jp.qilab.jle.javac.SampleInterface;

public class Temp {

	public static void main(String[] args) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) System.out.println(compiler);
		else {
		int res = compiler.run(null, null, null, "-version");
		System.out.println("戻り値：" + res);
		}
		java.awt.Dimension d = new java.awt.Dimension();
		java.lang.reflect.Method m = null;
		System.out.println(int.class.equals(Integer.class));
		
		
		new Temp().execute();
	}
	
	// 使い方
	public void execute() {
		// ソース文字列を生成
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		pw.println("package sample.string;");
		pw.println("import jp.qilab.jle.javac.SampleInterface;");
		pw.println("public class Sample3 implements SampleInterface {");
		pw.println("public String getValue() {");
		pw.println("return \"実行時にファイルを使わずコンパイル！\";");
		pw.println("}");
		pw.println("}");

		pw.close();
		String src = sw.toString();
		System.out.println("--- ソースを出力 ---");
		System.out.print(src);

	//コンパイルを実行
		Class<SampleInterface> c = compile("sample.string.Sample3", src); //キャストが間違っていても、ここではエラーにならない

	//インスタンスを生成して呼び出す
		SampleInterface s = null;
		try {
			s = c.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	//もしClassのキャストが間違っていたら、ここで例外が発生する
		System.out.println("--- 実行結果 ---");
		System.out.println(s.getValue());
	}
	
	
	
	protected DiagnosticListener<? super JavaFileObject> listener = new ErrorListener();

	protected JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

	/** コンパイル実行 */
	public <T> Class<T> compile(String class_name, String source_code) {
		JavaFileObject fo = new JavaSourceFromString(class_name, source_code);

		List<JavaFileObject> compilationUnits = Arrays.asList(fo);
		List<String> options = Arrays.asList(
					"-classpath", System.getProperty("java.class.path")
				);
		JavaFileManager manager = new ClassFileManager(compiler, listener);
		CompilationTask task = compiler.getTask(
						null,
						manager,	//出力ファイルを扱うマネージャー
						listener,	//エラー時の処理を行うリスナー（nullでもよい）
						options,	//コンパイルオプション
						null,
						compilationUnits	//コンパイル対象ファイル群
					);

		//コンパイル実行
		boolean successCompile = task.call();
		if (!successCompile) {
			throw new RuntimeException("コンパイル失敗：" + class_name);
		}

		ClassLoader cl = manager.getClassLoader(null);
		try {
			@SuppressWarnings("unchecked")
			Class<T> c = (Class<T>)cl.loadClass(class_name);
			return c;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
