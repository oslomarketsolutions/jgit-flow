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
import com.atlassian.maven.plugins.jgitflow.provider.ContextProvider;
import com.atlassian.maven.plugins.jgitflow.provider.JGitFlowProvider;
import com.atlassian.maven.plugins.jgitflow.provider.ProjectCacheKey;
import com.atlassian.maven.plugins.jgitflow.util.NamingUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.eclipse.jgit.api.Git;

@Component(role = UpdateFeaturePomsWithFinalVersionsCommand.class)
public class UpdateFeaturePomsWithFinalVersionsCommand implements ExtensionCommand
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
        String unprefixedBranchName = "";

        try
        {
            ReleaseContext ctx = contextProvider.getContext();

            if (!ctx.isEnableFeatureVersions())
            {
                return;
            }

            JGitFlow flow = jGitFlowProvider.gitFlow();

            unprefixedBranchName = branchHelper.getUnprefixedCurrentBranchName();

            //reload the reactor projects for release
            List<MavenProject> branchProjects = branchHelper.getProjectsForCurrentBranch();

            String featureVersion = NamingUtil.camelCaseOrSpaceToDashed(unprefixedBranchName);
            featureVersion = StringUtils.replace(featureVersion, "-", "_");

            pomUpdater.removeFeatureVersionFromSnapshotVersions(ProjectCacheKey.FEATURE_FINISH_LABEL, featureVersion, branchProjects);

            projectHelper.commitAllPoms(flow.git(), branchProjects, ctx.getScmCommentPrefix() + "updating poms for " + featureVersion + " version" + ctx.getScmCommentSuffix(), ctx.isNoVerify());

        }
        catch (Exception e)
        {
            throw new JGitFlowExtensionException("Error updating poms with feature versions for branch '" + unprefixedBranchName + "'",e);
        }
    }

    @Override
    public ExtensionFailStrategy failStrategy()
    {
        return ExtensionFailStrategy.ERROR;
    }
}
