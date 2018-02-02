package com.atlassian.maven.plugins.jgitflow.extension.command;

/*-
 * #%L
 * JGitFlow :: Maven Plugin
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

import java.util.List;

import com.atlassian.jgitflow.core.BranchType;
import com.atlassian.jgitflow.core.GitFlowConfiguration;
import com.atlassian.jgitflow.core.JGitFlow;
import com.atlassian.jgitflow.core.command.JGitFlowCommand;
import com.atlassian.jgitflow.core.exception.JGitFlowExtensionException;
import com.atlassian.jgitflow.core.extension.ExtensionCommand;
import com.atlassian.jgitflow.core.extension.ExtensionFailStrategy;
import com.atlassian.maven.plugins.jgitflow.ReleaseContext;
import com.atlassian.maven.plugins.jgitflow.VersionType;
import com.atlassian.maven.plugins.jgitflow.helper.BranchHelper;
import com.atlassian.maven.plugins.jgitflow.helper.PomUpdater;
import com.atlassian.maven.plugins.jgitflow.helper.ProjectHelper;
import com.atlassian.maven.plugins.jgitflow.provider.ContextProvider;
import com.atlassian.maven.plugins.jgitflow.provider.JGitFlowProvider;
import com.atlassian.maven.plugins.jgitflow.provider.ProjectCacheKey;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.eclipse.jgit.api.Git;

import static com.google.common.base.Preconditions.checkNotNull;

@Component(role = UpdatePomsWithSnapshotsCommand.class)
public class UpdatePomsWithSnapshotsCommand implements ExtensionCommand
{
    @Requirement
    private ContextProvider contextProvider;

    @Requirement
    private JGitFlowProvider jGitFlowProvider;

    @Requirement
    private PomUpdater pomUpdater;

    @Requirement
    private ProjectHelper projectHelper;

    @Requirement
    private BranchHelper branchHelper;

    @Override
    public void execute(GitFlowConfiguration configuration, Git git, JGitFlowCommand gitFlowCommand) throws JGitFlowExtensionException
    {
        ProjectCacheKey cacheKey = null;
        VersionType versionType = null;
        String versionSuffix = "";
        String unprefixedBranchName = "";
        boolean noVerify;

        try
        {
            BranchType branchType = branchHelper.getCurrentBranchType();

            ReleaseContext ctx = contextProvider.getContext();
            JGitFlow flow = jGitFlowProvider.gitFlow();
            boolean doSnapshots = true;
            
            switch (branchType)
            {
                case RELEASE:
                    cacheKey = ProjectCacheKey.RELEASE_START_LABEL;
                    versionType = VersionType.RELEASE;
                    versionSuffix = ctx.getReleaseBranchVersionSuffix();
                    doSnapshots = ctx.isReleaseSnapshots();
                    noVerify = ctx.isNoVerify();
                    break;

                case HOTFIX:
                    cacheKey = ProjectCacheKey.HOTFIX_LABEL;
                    versionType = VersionType.HOTFIX;
                    versionSuffix = "";
                    noVerify = ctx.isNoVerify();
                    break;

                default:
                    throw new JGitFlowExtensionException("Unsupported branch type '" + branchType.name() + "' while running " + this.getClass().getSimpleName() + " command");
            }

            checkNotNull(cacheKey);
            checkNotNull(versionType);

            unprefixedBranchName = branchHelper.getUnprefixedCurrentBranchName();

            //reload the reactor projects for release
            List<MavenProject> branchProjects = branchHelper.getProjectsForCurrentBranch();
            
            if(doSnapshots) {
                pomUpdater.addSnapshotToPomVersions(cacheKey, versionType, versionSuffix, branchProjects);
            }
            else
            {
                pomUpdater.removeSnapshotFromPomVersionsKeepSuffix(cacheKey, versionSuffix, branchProjects);
            }
            
            projectHelper.commitAllPoms(flow.git(), branchProjects, ctx.getScmCommentPrefix() + "updating poms for " + unprefixedBranchName + " branch with snapshot versions" + ctx.getScmCommentSuffix(), noVerify);
        }
        catch (Exception e)
        {
            throw new JGitFlowExtensionException("Error updating poms with snapshot versions for " + unprefixedBranchName + " branch: " + e.getMessage(), e);
        }
    }

    @Override
    public ExtensionFailStrategy failStrategy()
    {
        return ExtensionFailStrategy.ERROR;
    }
}
