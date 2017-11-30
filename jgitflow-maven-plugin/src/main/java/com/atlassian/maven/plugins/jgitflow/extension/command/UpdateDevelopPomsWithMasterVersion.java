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

import com.atlassian.jgitflow.core.GitFlowConfiguration;
import com.atlassian.jgitflow.core.JGitFlow;
import com.atlassian.jgitflow.core.command.JGitFlowCommand;
import com.atlassian.jgitflow.core.exception.JGitFlowExtensionException;
import com.atlassian.jgitflow.core.extension.ExtensionCommand;
import com.atlassian.jgitflow.core.extension.ExtensionFailStrategy;
import com.atlassian.maven.plugins.jgitflow.ReleaseContext;
import com.atlassian.maven.plugins.jgitflow.helper.BranchHelper;
import com.atlassian.maven.plugins.jgitflow.helper.PomUpdater;
import com.atlassian.maven.plugins.jgitflow.helper.ProjectHelper;
import com.atlassian.maven.plugins.jgitflow.manager.tasks.GetProjectsForBranch;
import com.atlassian.maven.plugins.jgitflow.provider.ContextProvider;
import com.atlassian.maven.plugins.jgitflow.provider.JGitFlowProvider;
import com.atlassian.maven.plugins.jgitflow.provider.ReactorProjectsProvider;
import com.atlassian.maven.plugins.jgitflow.provider.VersionCacheProvider;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.eclipse.jgit.api.Git;

@Component(role = UpdateDevelopPomsWithMasterVersion.class)
public class UpdateDevelopPomsWithMasterVersion implements ExtensionCommand
{
    @Requirement
    private JGitFlowProvider jGitFlowProvider;

    @Requirement
    private PomUpdater pomUpdater;

    @Requirement
    private BranchHelper branchHelper;

    @Requirement
    private GetProjectsForBranch getProjectsForBranch;

    @Requirement
    private ReactorProjectsProvider reactorProjectsProvider;

    @Requirement
    private ProjectHelper projectHelper;

    @Requirement
    private ContextProvider contextProvider;

    @Requirement
    private VersionCacheProvider versionCacheProvider;

    @Override
    public void execute(GitFlowConfiguration configuration, Git git, JGitFlowCommand gitFlowCommand) throws JGitFlowExtensionException
    {
        try
        {
            String originalBranchName = branchHelper.getCurrentBranchName();

            ReleaseContext ctx = contextProvider.getContext();

            JGitFlow flow = jGitFlowProvider.gitFlow();

            List<MavenProject> developProjects = getProjectsForBranch.run(flow.getDevelopBranchName(), reactorProjectsProvider.getReactorProjects());
            List<MavenProject> masterProjects = getProjectsForBranch.run(flow.getMasterBranchName(), reactorProjectsProvider.getReactorProjects());

            //cache the current develop versions
            flow.git().checkout().setName(flow.getDevelopBranchName()).call();
            versionCacheProvider.cacheCurrentBranchVersions();

            pomUpdater.copyPomVersionsFromProject(masterProjects, developProjects);

            projectHelper.commitAllPoms(flow.git(), developProjects, ctx.getScmCommentPrefix() + "updating develop poms to master versions to avoid merge conflicts" + ctx.getScmCommentSuffix(), ctx.isNoVerify());

            flow.git().checkout().setName(originalBranchName).call();

        }
        catch (Exception e)
        {
            throw new JGitFlowExtensionException("Error updating release poms to develop version", e);
        }
    }

    @Override
    public ExtensionFailStrategy failStrategy()
    {
        return null;
    }
}
