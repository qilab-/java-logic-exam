package jp.qilab.jle.javac;

import javax.tools.*;

public class ErrorListener implements DiagnosticListener<JavaFileObject> {

	// コンパイルエラーが起きたときに呼ばれる
	public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
		System.out.println("▼report start");
		System.out.println("errcode：" + diagnostic.getCode());
		System.out.println("line   ：" + diagnostic.getLineNumber());
		System.out.println("column ：" + diagnostic.getColumnNumber());
		System.out.println("message：" + diagnostic.getMessage(null));
		//System.out.println(diagnostic.toString());
		System.out.println("▲report end");
	}
}
