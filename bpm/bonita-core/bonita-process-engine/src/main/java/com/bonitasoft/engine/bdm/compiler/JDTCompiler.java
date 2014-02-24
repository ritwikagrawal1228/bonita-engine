package com.bonitasoft.engine.bdm.compiler;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.compiler.batch.BatchCompiler;

/**
 * Compiler based on JDTCompiler
 * 
 * @author Colin PUY
 */
public class JDTCompiler {

    private static final String COMPILER_COMPLIANCE_LEVEL = "-1.6";

    public void compile(Collection<File> filesToBeCompiled, File outputdirectory) throws CompilationException {
        compile(filesToBeCompiled, outputdirectory, null);
    }

    public void compile(Collection<File> filesToBeCompiled, File outputdirectory, String classpath) throws CompilationException {
        String[] commandLine = buildCommandLineArguments(filesToBeCompiled, outputdirectory, classpath);
        launchCompiler(commandLine);
    }

    private String[] buildCommandLineArguments(Collection<File> files, File outputdirectory, String classpath) {
        List<String> arguments = new ArrayList<String>();
        arguments.add(COMPILER_COMPLIANCE_LEVEL);
        arguments.addAll(outputDirectoryArguments(outputdirectory));
        arguments.addAll(classpathArguments(classpath));
        arguments.addAll(filesToBeCompiledArguments(files));
        return arguments.toArray(new String[arguments.size()]);
    }

    private List<String> filesToBeCompiledArguments(Collection<File> files) {
        List<String> arguments = new ArrayList<String>(files.size());
        for (File file : files) {
            arguments.add(file.getAbsolutePath());
        }
        return arguments;
    }

    private List<String> classpathArguments(String classpath) {
        if (classpath == null || classpath.isEmpty()) {
            return emptyList();
        }
        return asList("-cp", classpath);
    }

    private List<String> outputDirectoryArguments(File outputdirectory) {
        if (outputdirectory == null) {
            return emptyList();
        }
        return asList("-d", outputdirectory.getAbsolutePath());
    }

    private void launchCompiler(String[] commandLine) throws CompilationException {
        PrintWriter outWriter = new PrintWriter(new ByteArrayOutputStream());
        // closing outwriter since we don't want to see compilation out stream
        outWriter.close();
        
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        PrintWriter errorWriter = new PrintWriter(errorStream);
        try {
            doCompilation(commandLine, outWriter, errorStream, errorWriter);
        } finally {
            // no need to close OutputStream, printWriter is doing it for us
            if (errorWriter != null) errorWriter.close(); 
        }
    }

    private void doCompilation(String[] commandLine, PrintWriter outWriter, ByteArrayOutputStream errorStream, PrintWriter errorWriter) throws CompilationException {
        boolean succeeded = BatchCompiler.compile(commandLine, outWriter, errorWriter, new DummyCompilationProgress());
        if (!succeeded) {
            throw new CompilationException(new String(errorStream.toByteArray()));
        }
    }
}
