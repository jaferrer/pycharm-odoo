package dev.ngocta.pycharm.odoo.javascript;

import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OdooJSModuleFindUsagesProvider implements FindUsagesProvider {
    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof OdooJSModule;
    }

    @Override
    @Nullable
    public String getHelpId(@NotNull PsiElement psiElement) {
        return "";
    }

    @Override
    @Nls
    @NotNull
    public String getType(@NotNull PsiElement element) {
        return "";
    }

    @Override
    @Nls
    @NotNull
    public String getDescriptiveName(@NotNull PsiElement element) {
        return "";
    }

    @Override
    @Nls
    @NotNull
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return "";
    }
}
