package com.atlassian.jgitflow.core;

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

import java.io.File;

import org.eclipse.jgit.api.Git;

/**
 * This class exposes information about a JGitFlow instance without leaking any of the operational methods.
 * This is mainly to pass into extensions that need branch information but should not kick of a new jgitflow process
 */
public class JGitFlowInfo
{
    private final Git git;
    private final JGitFlowReporter reporter = JGitFlowReporter.get();
    private final String masterBranchName;
    private final String developBranchName;
    private final String featureBranchPrefix;
    private final String releaseBranchPrefix;
    private final String hotfixBranchPrefix;
    private final String supportBranchPrefix;
    private final String versionTagPrefix;

    public JGitFlowInfo(Git git, GitFlowConfiguration gfConfig)
    {
        this.git = git;
        this.developBranchName = gfConfig.getDevelop();
        this.featureBranchPrefix = gfConfig.getPrefixValue(JGitFlowConstants.PREFIXES.FEATURE.configKey());
        this.hotfixBranchPrefix = gfConfig.getPrefixValue(JGitFlowConstants.PREFIXES.HOTFIX.configKey());
        this.masterBranchName = gfConfig.getMaster();
        this.releaseBranchPrefix = gfConfig.getPrefixValue(JGitFlowConstants.PREFIXES.RELEASE.configKey());
        this.supportBranchPrefix = gfConfig.getPrefixValue(JGitFlowConstants.PREFIXES.SUPPORT.configKey());
        this.versionTagPrefix = gfConfig.getPrefixValue(JGitFlowConstants.PREFIXES.VERSIONTAG.configKey());
    }

    /**
     * Returns the {@link org.eclipse.jgit.api.Git} instance used by this JGitFlow instance
     *
     * @return
     */
    public Git git()
    {
        return git;
    }

    /**
     * Returns the project root directory as a File
     *
     * @return
     */
    public File getProjectRoot()
    {
        return git.getRepository().getWorkTree();
    }

    /**
     * Returns the master branch name configured for this instance's git flow project
     *
     * @return
     */
    public String getMasterBranchName()
    {
        return masterBranchName;
    }

    /**
     * Returns the develop branch name configured for this instance's git flow project
     *
     * @return
     */
    public String getDevelopBranchName()
    {
        return developBranchName;
    }

    /**
     * Returns the feature branch prefix configured for this instance's git flow project
     *
     * @return
     */
    public String getFeatureBranchPrefix()
    {
        return featureBranchPrefix;
    }

    /**
     * Returns the release branch prefix configured for this instance's git flow project
     *
     * @return
     */
    public String getReleaseBranchPrefix()
    {
        return releaseBranchPrefix;
    }

    /**
     * Returns the hotfix branch prefix configured for this instance's git flow project
     *
     * @return
     */
    public String getHotfixBranchPrefix()
    {
        return hotfixBranchPrefix;
    }

    public String getSupportBranchPrefix()
    {
        return supportBranchPrefix;
    }

    /**
     * Returns the versiontag prefix configured for this instance's git flow project
     *
     * @return
     */
    public String getVersionTagPrefix()
    {
        return versionTagPrefix;
    }

    public JGitFlowReporter getReporter()
    {
        return reporter;
    }
}
