package fnz.control

import static org.codehaus.groovy.ast.tools.GeneralUtils.*

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.transform.AbstractASTTransformation

/**
 * This transformation makes possible to use the unless(boolean) expression
 * globally in your code.
 *
 * @since 1.0.1
 *
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
class UnlessAst extends AbstractASTTransformation {

    void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
         sourceUnit.AST.classes.each { ClassNode clazzNode ->
             new UnlessAstVisitor(sourceUnit).visitClass(clazzNode)
         }
    }

}