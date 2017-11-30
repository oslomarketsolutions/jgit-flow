package com.atlassian.maven.plugins.jgitflow.manager;

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
import com.atlassian.jgitflow.core.JGitFlow;
import com.atlassian.jgitflow.core.JGitFlowReporter;
import com.atlassian.jgitflow.core.ReleaseMergeResult;
import com.atlassian.jgitflow.core.exception.JGitFlowException;
import com.atlassian.maven.plugins.jgitflow.ReleaseContext;
import com.atlassian.maven.plugins.jgitflow.exception.MavenJGitFlowException;
import com.atlassian.maven.plugins.jgitflow.extension.HotfixFinishPluginExtension;
import com.atlassian.maven.plugins.jgitflow.extension.HotfixStartPluginExtension;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

/**
 * @since version
 */
@Component(role = FlowReleaseManager.class, hint = "hotfix")
public class DefaultFlowHotfixManager extends AbstractProductionBranchManager
{
    @Requirement
    private HotfixStartPluginExtension startExtension;

    @Requirement
    private HotfixFinishPluginExtension finishExtension;

    public DefaultFlowHotfixManager()
    {
        super(BranchType.HOTFIX);
    }

    @Override
    public void start(ReleaseContext ctx, List<MavenProject> reactorProjects, MavenSession session) throws MavenJGitFlowException
    {
        JGitFlow flow = null;

        try
        {
            String hotfixLabel = getStartLabelAndRunPreflight(ctx, reactorProjects, session);

            flow = jGitFlowProvider.gitFlow();

            startExtension.init(ctx.getHotfixStartExtension());

            flow.hotfixStart(hotfixLabel)
                .setAllowUntracked(ctx.isAllowUntracked())
                .setPush(ctx.isPushHotfixes())
                .setNoVerify(ctx.isNoVerify())
                .setStartCommit(ctx.getStartCommit())
                .setScmMessagePrefix(ctx.getScmCommentPrefix())
                .setScmMessageSuffix(ctx.getScmCommentSuffix())
                .setExtension(startExtension)
                .call();
        }
        catch (JGitFlowException e)
        {
            throw new MavenJGitFlowException("Error starting hotfix: " + e.getMessage(), e);
        }
        finally
        {
            if (null != flow)
            {
                JGitFlowReporter.get().flush();
            }
        }
    }

    @Override
    public void finish(ReleaseContext ctx, List<MavenProject> reactorProjects, MavenSession session) throws MavenJGitFlowException
    {
        JGitFlow flow = null;

        try
        {
            finishExtension.init(ctx.getHotfixFinishExtension());
            String hotfixLabel = getFinishLabelAndRunPreflight(ctx, reactorProjects, session);

            flow = jGitFlowProvider.gitFlow();

            getLogger().info("running jgitflow hotfix finish...");
            ReleaseMergeResult mergeResult = flow.hotfixFinish(hotfixLabel)
                                                 .setPush(ctx.isPushHotfixes())
                                                 .setKeepBranch(ctx.isKeepBranch())
                                                 .setNoTag(ctx.isNoTag())
                                                 .setNoVerify(ctx.isNoVerify())
                                                 .setAllowUntracked(ctx.isAllowUntracked())
                                                 .setScmMessagePrefix(ctx.getScmCommentPrefix())
                                                 .setScmMessageSuffix(ctx.getScmCommentSuffix())
                                                 .setExtension(finishExtension)
                                                 .call();

            if (!mergeResult.wasSuccessful())
            {
                if (mergeResult.masterHasProblems())
                {
                    getLogger().error("Error merging into " + flow.getMasterBranchName() + ":");
                    getLogger().error(mergeResult.getMasterResult().toString());
                    getLogger().error("see .git/jgitflow.log for more info");
                }

                if (mergeResult.developHasProblems())
                {
                    getLogger().error("Error merging into " + flow.getDevelopBranchName() + ":");
                    getLogger().error(mergeResult.getDevelopResult().toString());
                    getLogger().error("see .git/jgitflow.log for more info");
                }

                throw new MavenJGitFlowException("Error while merging hotfix!");
            }

        }
        catch (JGitFlowException e)
        {
            throw new MavenJGitFlowException("Error finishing hotfix: " + e.getMessage(), e);
        }
        finally
        {
            if (null != flow)
            {
                JGitFlowReporter.get().flush();
            }
        }
    }
}
