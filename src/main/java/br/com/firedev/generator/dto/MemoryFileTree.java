package br.com.firedev.generator.dto;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MemoryFileTree extends TreeMap<String, MemoryFileTree> {

    private static final long serialVersionUID = 8485418269716210181L;
    private String fileName;
    private boolean directory;
    private byte[] content;

    public MemoryFileTree() {
        this.directory = false;
    }

    public MemoryFileTree(String fileName, boolean directory) {
        this.fileName = fileName;
        this.directory = directory;
    }

    public MemoryFileTree(File file) {
        this.fileName = file.getName();
        this.directory = file.isDirectory();
        if (isDirectory()) {
            for (var child : file.listFiles()) {
                this.put(child.getName(), new MemoryFileTree(child));
            }
        } else {
            try {
                this.content = Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(fileName);
        buffer.append('\n');
        for (var it = this.values().iterator(); it.hasNext();) {
            var next = it.next();
            if (it.hasNext()) {
                next.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
            } else {
                next.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
        }
    }

    @Override
    public String toString() {
        var buffer = new StringBuilder(50);
        print(buffer, "", "");
        return buffer.toString();
    }

    public MemoryFileTree getOrCreateDir(String name) {
        return this.computeIfAbsent(name, k -> new MemoryFileTree(name, true));
    }

    public MemoryFileTree putFile(String name, String content) {
        var childFile = new MemoryFileTree(name, false);
        childFile.setContent(content.getBytes(StandardCharsets.UTF_8));
        return this.put(name, childFile);
    }

    public byte[] toZip() {
        try (var byteArrayOutputStream = new ByteArrayOutputStream();
                var bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
                var zipOutputStream = new ZipOutputStream(bufferedOutputStream);) {
            this.toZipChild(zipOutputStream, this.fileName);
            zipOutputStream.finish();
            zipOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    private void toZipChild(ZipOutputStream zipOutputStream, String fileName) throws IOException {
        if (this.isDirectory()) {
            if (!fileName.endsWith("/")) {
                fileName += "/";
            }
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            zipOutputStream.closeEntry();
            for (var childFile : this.values()) {
                var childName = fileName + childFile.getFileName();
                childFile.toZipChild(zipOutputStream, childName);
            }
        } else {
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            zipOutputStream.write(this.content, 0, this.content.length);
        }
    }
}
