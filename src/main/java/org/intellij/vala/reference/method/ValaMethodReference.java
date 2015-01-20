package org.intellij.vala.reference.method;


import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.vala.psi.ValaDeclaration;
import org.intellij.vala.psi.ValaDeclarationContainer;
import org.intellij.vala.psi.ValaMethodDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.intellij.vala.psi.impl.ValaPsiElementUtil.findTypeDeclaration;

public class ValaMethodReference extends PsiReferenceBase<PsiNamedElement> {

    private PsiElement objectVariable;

    public ValaMethodReference(PsiNamedElement element) {
        this(null, element);
    }

    public ValaMethodReference(PsiElement objectVariable, PsiNamedElement element) {
        super(element, new TextRange(0, element.getName().length()));
        this.objectVariable = objectVariable;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        if (objectVariable != null) {
            return resolveAsOtherObjectMethodCallReference();
        }
        return resolveInContainingClass();
    }

    private PsiElement resolveAsOtherObjectMethodCallReference() {
        ValaDeclaration parentType = resolveObjectType();
        if (parentType == null) {
            return null;
        }
        if (parentType instanceof ValaDeclarationContainer) {
            return getMatchingMethodDeclaration(myElement, (ValaDeclarationContainer) parentType);
        }
        return null;
    }

    private PsiElement resolveInContainingClass() {
        ValaDeclarationContainer classDeclaration = PsiTreeUtil.getParentOfType(myElement, ValaDeclarationContainer.class, false);
        if (classDeclaration != null) {
            return getMatchingMethodDeclaration(myElement, classDeclaration);
        }
        return null;
    }

    private static PsiElement getMatchingMethodDeclaration(PsiNamedElement name, ValaDeclarationContainer classDeclaration) {
        for (ValaDeclaration namespaceMember : classDeclaration.getDeclarations()) {
            if (namespaceMember instanceof ValaMethodDeclaration) {
                ValaMethodDeclaration methodDeclaration = (ValaMethodDeclaration) namespaceMember;
                if (name.getName().equals(methodDeclaration.getName())) {
                    return methodDeclaration;
                }
            }
        }
        return null;
    }

    private ValaDeclaration resolveObjectType() {
        PsiReference parentRef = objectVariable.getReference();
        if (parentRef == null) {
            return null;
        }
        PsiElement resolved = parentRef.resolve();
        if (resolved != null) {
            return findTypeDeclaration(resolved);
        }
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }
}
