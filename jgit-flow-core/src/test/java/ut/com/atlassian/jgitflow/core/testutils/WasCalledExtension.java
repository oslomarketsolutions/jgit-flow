package ut.com.atlassian.jgitflow.core.testutils;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.jgitflow.core.GitFlowConfiguration;
import com.atlassian.jgitflow.core.JGitFlowReporter;
import com.atlassian.jgitflow.core.exception.JGitFlowExtensionException;
import com.atlassian.jgitflow.core.extension.ExtensionCommand;
import com.atlassian.jgitflow.core.extension.ExtensionFailStrategy;
import com.atlassian.jgitflow.core.extension.JGitFlowExtension;

import org.eclipse.jgit.api.Git;

public class WasCalledExtension implements ExtensionCommand
{
    private boolean methodCalled;
    private boolean withException;
    private ExtensionFailStrategy failStrategy;
    
    public WasCalledExtension()
    {
        this(false);
    }

    public WasCalledExtension(boolean withException)
    {
        this.methodCalled = false;
        this.withException = withException;
        this.failStrategy = ExtensionFailStrategy.WARN;
    }

    @Override
    public void execute(GitFlowConfiguration configuration, Git git, JGitFlowReporter reporter) throws JGitFlowExtensionException
    {
        this.methodCalled = true;
        if(withException)
        {
            throw new JGitFlowExtensionException("Exception!!!");
        }
    }

    @Override
    public ExtensionFailStrategy failStrategy()
    {
        return failStrategy;
    }

    public void setFailStrategy(ExtensionFailStrategy failStrategy)
    {
        this.failStrategy = failStrategy;
    }

    public boolean wasCalled()
    {
        return methodCalled;
    }
}