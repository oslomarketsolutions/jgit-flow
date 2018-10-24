package com.atlassian.maven.plugins.jgitflow.mojo;

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

import com.atlassian.maven.plugins.jgitflow.ReleaseContext;
import com.atlassian.maven.plugins.jgitflow.exception.MavenJGitFlowException;
import com.atlassian.maven.plugins.jgitflow.manager.FlowReleaseManager;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * @since version
 */
@Mojo(name = "feature-start", aggregator = true)
public class FeatureStartMojo extends AbstractJGitFlowMojo
{

    /**
     * Default name of the feature. This option is primarily useful when starting the goal in non-interactive mode.
     */
    @Parameter(property = "featureName")
    private String featureName;

    /**
     * Whether to append the feature name to the version on the feature branch.
     */
    @Parameter(defaultValue = "false", property = "enableFeatureVersions")
    private boolean enableFeatureVersions = false;

    /**
     * Whether to push feature branches to the remote upstream.
     */
    @Parameter(defaultValue = "false", property = "pushFeatures")
    private boolean pushFeatures = false;

    /**
     * A SHA, short SHA, or branch name to use as the starting point for the new branch
     */
    @Parameter(property = "startCommit", defaultValue = "")
    private String startCommit = "";

    @Component(hint = "feature")
    FlowReleaseManager releaseManager;

    /**
     * If set to true, skip pre-commit and message hooks when committing.
     */
    @Parameter(defaultValue = "false", property = "noVerifyFeature")
    protected boolean noVerifyFeature = false;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        ReleaseContext ctx = new ReleaseContext(getBasedir());
        ctx.setInteractive(getSettings().isInteractiveMode())
           .setDefaultFeatureName(featureName)
           .setEnableFeatureVersions(enableFeatureVersions)
           .setEnableSshAgent(enableSshAgent)
           .setAllowUntracked(allowUntracked)
           .setAllowSnapshots(allowSnapshots)
           .setPushFeatures(pushFeatures)
           .setStartCommit(startCommit)
           .setAllowRemote(isRemoteAllowed())
           .setAlwaysUpdateOrigin(alwaysUpdateOrigin)
           .setDefaultOriginUrl(defaultOriginUrl)
           .setPullMaster(pullMaster)
           .setPullDevelop(pullDevelop)
           .setScmCommentPrefix(scmCommentPrefix)
           .setScmCommentSuffix(scmCommentSuffix)
           .setUseReleaseProfile(false)
           .setUsername(username)
           .setPassword(password)
                .setEol(eol)
           .setFlowInitContext(getFlowInitContext().getJGitFlowContext())
           .setNoVerify(noVerifyFeature);

        try
        {
            releaseManager.start(ctx, getReactorProjects(), session);
        }
        catch (MavenJGitFlowException e)
        {
            throw new MojoExecutionException("Error starting feature: " + e.getMessage(), e);
        }
    }
}
