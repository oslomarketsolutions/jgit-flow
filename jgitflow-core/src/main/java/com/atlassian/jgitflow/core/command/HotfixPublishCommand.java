package com.atlassian.jgitflow.core.command;

/*-
 * #%L
 * JGitFlow :: Core
 * %%
 * Copyright (C) 2017 Atlassian Pty, LTD, Ultreia.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;

import com.atlassian.jgitflow.core.GitFlowConfiguration;
import com.atlassian.jgitflow.core.JGitFlowConstants;
import com.atlassian.jgitflow.core.exception.*;
import com.atlassian.jgitflow.core.extension.JGitFlowExtension;
import com.atlassian.jgitflow.core.extension.impl.EmptyHotfixStartExtension;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RefSpec;

/**
 * Publishes hotfix branch to the remote repository
 * <p></p>
 * Examples ({@code flow} is a {@link com.atlassian.jgitflow.core.JGitFlow} instance):
 * <p></p>
 * Publish a hotfix:
 * <p></p>
 * <pre>
 * flow.hotfixPublish(&quot;hotfix&quot;).call();
 * </pre>
 */
public class HotfixPublishCommand extends AbstractGitFlowCommand<HotfixPublishCommand, Void>
{
    private static final String SHORT_NAME = "hotfix-publish";

    /**
     * Create a new hotfix publish command instance.
     * <p></p>
     * An instance of this class is usually obtained by calling
     * {@link com.atlassian.jgitflow.core.JGitFlow#hotfixPublish(String)}
     *
     * @param git      The git instance to use
     * @param gfConfig The GitFlowConfiguration to use
     */
    public HotfixPublishCommand(String branchName, Git git, GitFlowConfiguration gfConfig)
    {
        super(branchName, git, gfConfig);
    }

    /**
     * @return nothing
     * @throws com.atlassian.jgitflow.core.exception.NotInitializedException
     * @throws com.atlassian.jgitflow.core.exception.JGitFlowGitAPIException
     * @throws com.atlassian.jgitflow.core.exception.DirtyWorkingTreeException
     * @throws com.atlassian.jgitflow.core.exception.JGitFlowIOException
     * @throws com.atlassian.jgitflow.core.exception.LocalBranchMissingException
     * @throws com.atlassian.jgitflow.core.exception.RemoteBranchExistsException
     */
    @Override
    public Void call() throws NotInitializedException, JGitFlowGitAPIException, DirtyWorkingTreeException, JGitFlowIOException, LocalBranchMissingException, RemoteBranchExistsException, JGitFlowExtensionException
    {
        JGitFlowExtension extension = new EmptyHotfixStartExtension();

        String prefixedBranchName = runBeforeAndGetPrefixedBranchName(extension.before(), JGitFlowConstants.PREFIXES.HOTFIX);
        enforcer().requireGitFlowInitialized();
        enforcer().requireCleanWorkingTree(isAllowUntracked());
        enforcer().requireLocalBranchExists(prefixedBranchName);

        try
        {
            doFetchIfNeeded(extension);

            enforcer().requireRemoteBranchAbsent(prefixedBranchName);

            //create remote feature branch
            RefSpec branchSpec = new RefSpec(prefixedBranchName + ":" + Constants.R_HEADS + prefixedBranchName);
            git.push().setRemote(Constants.DEFAULT_REMOTE_NAME).setRefSpecs(branchSpec).call();
            git.fetch().setRemote(Constants.DEFAULT_REMOTE_NAME).call();

            //setup tracking
            StoredConfig config = git.getRepository().getConfig();
            config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, prefixedBranchName, ConfigConstants.CONFIG_KEY_REMOTE, Constants.DEFAULT_REMOTE_NAME);
            config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, prefixedBranchName, ConfigConstants.CONFIG_KEY_MERGE, Constants.R_HEADS + prefixedBranchName);
            config.save();
            config.load();
            //checkout the branch
            git.checkout().setName(prefixedBranchName).call();

        }
        catch (IOException e)
        {
            throw new JGitFlowIOException(e);
        }
        catch (GitAPIException e)
        {
            throw new JGitFlowGitAPIException(e);
        }
        catch (ConfigInvalidException e)
        {
            throw new JGitFlowGitAPIException(e);
        }
        finally
        {
            reporter.endCommand();
            reporter.flush();
        }

        return null;
    }

    @Override
    protected String getCommandName()
    {
        return SHORT_NAME;
    }
}
