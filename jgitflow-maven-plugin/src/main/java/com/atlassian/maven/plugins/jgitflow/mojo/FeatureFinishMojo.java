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
@Mojo(name = "feature-finish", aggregator = true)
public class FeatureFinishMojo extends AbstractJGitFlowMojo
{

    /**
     * Default name of the feature. This option is primarily useful when starting the goal in non-interactive mode.
     */
    @Parameter(property = "featureName", defaultValue = "")
    private String featureName = "";

    /**
     * Whether to keep the feature branch after finishing the release.
     * If set to false, the branch will be deleted.
     */
    @Parameter(defaultValue = "false", property = "keepBranch")
    private boolean keepBranch = false;

    /**
     * Whether to squash commits into a single commit before merging.
     */
    @Parameter(defaultValue = "false", property = "squash")
    private boolean squash = false;

    /**
     * Whether to rebase the feature branch before merging.
     */
    @Parameter(defaultValue = "false", property = "featureRebase")
    private boolean featureRebase = false;

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
     * Whether to use NO_FF as the merge strategy
     */
    @Parameter(defaultValue = "false", property = "suppressFastForward")
    private boolean suppressFastForward = false;

    /**
     * Whether to turn off merging changes from the feature branch to develop
     */
    @Parameter(defaultValue = "false", property = "noFeatureMerge")
    private boolean noFeatureMerge = false;

    /**
     * Whether to turn off project building. If true the project will NOT be built during feature finish
     */
    @Parameter(defaultValue = "false", property = "noFeatureBuild")
    private boolean noFeatureBuild = false;

    /**
     * If set to true, only the first parent/project version will be used across all version updates
     * @see <a href="https://ecosystem.atlassian.net/browse/MJF-204">https://ecosystem.atlassian.net/browse/MJF-204</a>
     */
    @Parameter(defaultValue = "false", property = "consistentProjectVersions")
    protected boolean consistentProjectVersions = false;
    
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
           .setNoDeploy(true)
           .setEnableFeatureVersions(enableFeatureVersions)
           .setKeepBranch(keepBranch)
           .setSquash(squash)
           .setFeatureRebase(featureRebase)
           .setDefaultFeatureName(featureName)
           .setEnableSshAgent(enableSshAgent)
           .setAllowUntracked(allowUntracked)
           .setAllowSnapshots(allowSnapshots)
           .setPushFeatures(pushFeatures)
           .setAllowRemote(isRemoteAllowed())
           .setAlwaysUpdateOrigin(alwaysUpdateOrigin)
           .setNoFeatureMerge(noFeatureMerge)
           .setSuppressFastForward(suppressFastForward)
           .setNoBuild(noFeatureBuild)
           .setDefaultOriginUrl(defaultOriginUrl)
           .setScmCommentPrefix(scmCommentPrefix)
           .setScmCommentSuffix(scmCommentSuffix)
           .setUsername(username)
           .setPassword(password)
           .setPullMaster(pullMaster)
           .setPullDevelop(pullDevelop)
           .setUseReleaseProfile(false)
                .setEol(eol)
           .setConsistentProjectVersions(consistentProjectVersions)
           .setFlowInitContext(getFlowInitContext().getJGitFlowContext())
           .setNoVerify(noVerifyFeature);

        try
        {
            releaseManager.finish(ctx, getReactorProjects(), session);
        }
        catch (MavenJGitFlowException e)
        {
            throw new MojoExecutionException("Error finishing feature: " + e.getMessage(), e);
        }
    }
}
