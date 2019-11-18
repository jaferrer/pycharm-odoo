package dev.ngocta.pycharm.odoo;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopesCore;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.stubs.PyClassNameIndex;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class OdooModelIndex extends ScalarIndexExtension<String> {
    public static final @NotNull ID<String, Void> NAME = ID.create("odoo.model");

    private DataIndexer<String, Void, FileContent> myDataIndexer = inputData -> {
        HashMap<String, Void> result = new HashMap<>();
        VirtualFile virtualFile = inputData.getFile();
        PsiFile psiFile = PsiManager.getInstance(inputData.getProject()).findFile(virtualFile);
        if (psiFile instanceof PyFile) {
            PyFile pyFile = (PyFile) psiFile;
            pyFile.getTopLevelClasses().forEach(pyClass -> {
                OdooModelInfo info = OdooModelInfo.readFromClass(pyClass);
                if (info != null) {
                    result.put(info.getName(), null);
                }
            });
        }
        return result;
    };

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return NAME;
    }

    @NotNull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer() {
        return myDataIndexer;
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return EnumeratorStringDescriptor.INSTANCE;
    }

    @Override
    public int getVersion() {
        return 2;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return OdooModelInputFilter.INSTANCE;
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @NotNull
    public static List<PyClass> findModelClasses(@NotNull String model, @NotNull PsiDirectory module) {
        List<PyClass> result = new LinkedList<>();
        FileBasedIndex index = FileBasedIndex.getInstance();
        Set<VirtualFile> files = new HashSet<>();
        index.getFilesWithKey(NAME, Collections.singleton(model), files::add,
                GlobalSearchScopesCore.directoryScope(module, true));
        Project project = module.getProject();
        PsiManager psiManager = PsiManager.getInstance(project);
        files.forEach(file -> {
            PsiFile psiFile = psiManager.findFile(file);
            if (psiFile instanceof PyFile) {
                ((PyFile) psiFile).getTopLevelClasses().forEach(pyClass -> {
                    OdooModelInfo info = OdooModelInfo.readFromClass(pyClass);
                    if (info != null && info.getName().equals(model)) {
                        result.add(pyClass);
                    }
                });
            }
        });
        return result;
    }

    @NotNull
    public static List<PyClass> findModelClasses(@NotNull String model, @NotNull String moduleName, @NotNull Project project) {
        PsiDirectory module = OdooModuleIndex.getModuleByName(moduleName, project);
        if (module != null) {
            return findModelClasses(model, module);
        }
        return Collections.emptyList();
    }
}