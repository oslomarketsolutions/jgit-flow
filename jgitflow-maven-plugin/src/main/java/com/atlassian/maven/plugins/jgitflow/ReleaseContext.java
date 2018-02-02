package com.atlassian.maven.plugins.jgitflow;

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

import com.atlassian.jgitflow.core.InitContext;
import com.atlassian.maven.jgitflow.api.MavenHotfixFinishExtension;
import com.atlassian.maven.jgitflow.api.MavenHotfixStartExtension;
import com.atlassian.maven.jgitflow.api.MavenReleaseFinishExtension;
import com.atlassian.maven.jgitflow.api.MavenReleaseStartExtension;
import com.google.common.base.Strings;

import java.io.File;

/**
 * @since version
 */
public class ReleaseContext
{
    private boolean allowSnapshots;
    private boolean interactive;
    private boolean autoVersionSubmodules;
    private boolean updateDependencies;
    private boolean pushFeatures;
    private boolean pushReleases;
    private boolean pushHotfixes;
    private boolean keepBranch;
    private boolean squash;
    private boolean noTag;
    private boolean noDeploy;
    private boolean noBuild;
    private boolean featureRebase;
    private boolean useReleaseProfile;
    private boolean enableFeatureVersions;
    private String args;
    private String goals;
    private String tagMessage;
    private String defaultReleaseVersion;
    private String defaultDevelopmentVersion;
    private String versionNumberToIncrement;
    private boolean incrementDevelopFromReleaseVersion;
    private String defaultFeatureName;
    private String releaseBranchVersionSuffix;
    private InitContext flowInitContext;
    private final File baseDir;
    private boolean enableSshAgent;
    private boolean noReleaseMerge;
    private boolean noFeatureMerge;
    private boolean suppressFastForward;
    private boolean allowUntracked;
    private boolean allowRemote;
    private boolean pullMaster;
    private boolean pullDevelop;
    private String startCommit;
    private String defaultOriginUrl;
    private String scmCommentPrefix;
    private String scmCommentSuffix;
    private String username;
    private String password;
    private boolean alwaysUpdateOrigin;
    private boolean consistentProjectVersions;
    private MavenReleaseStartExtension releaseStartExtension;
    private MavenReleaseFinishExtension releaseFinishExtension;
    private MavenHotfixStartExtension hotfixStartExtension;
    private MavenHotfixFinishExtension hotfixFinishExtension;
    private String eol;
    private boolean releaseSnapshots;
    private boolean addScmCommentSuffixOnMerge;
    private boolean noVerify;
    private boolean addFixLevel;

    public ReleaseContext(File baseDir)
    {
        this.baseDir = baseDir;
        this.allowSnapshots = false;
        this.defaultReleaseVersion = null;
        this.defaultDevelopmentVersion = null;
        this.versionNumberToIncrement = null;
        this.interactive = true;
        this.autoVersionSubmodules = false;
        this.updateDependencies = true;
        this.pushFeatures = false;
        this.pushReleases = false;
        this.pushHotfixes = false;
        this.keepBranch = false;
        this.squash = false;
        this.noTag = false;
        this.noDeploy = false;
        this.noBuild = false;
        this.featureRebase = false;
        this.useReleaseProfile = true;
        this.args = "";
        this.goals = "clean deploy";
        this.startCommit = "";
        this.releaseBranchVersionSuffix = "release";
        this.enableFeatureVersions = false;
        this.tagMessage = "tagging release ${version}";
        this.flowInitContext = new InitContext();
        this.enableSshAgent = false;
        this.allowUntracked = false;
        this.noReleaseMerge = false;
        this.noFeatureMerge = false;
        this.allowRemote = true;
        this.defaultOriginUrl = "";
        this.scmCommentPrefix = "";
        this.scmCommentSuffix = "";
        this.pullMaster = false;
        this.pullDevelop = false;
        this.username = "";
        this.password = "";
        this.alwaysUpdateOrigin = true;
        this.consistentProjectVersions = false;
        this.releaseStartExtension = null;
        this.releaseFinishExtension = null;
        this.hotfixStartExtension = null;
        this.hotfixFinishExtension = null;
        this.eol = "";
        this.versionNumberToIncrement = "2";
        this.releaseSnapshots = true;
        this.noVerify = false;
        this.noVerify = false;
    }

    public boolean isAllowSnapshots()
    {
        return allowSnapshots;
    }

    public ReleaseContext setAllowSnapshots(boolean allowSnapshots)
    {
        this.allowSnapshots = allowSnapshots;
        return this;
    }

    public String getDefaultReleaseVersion()
    {
        return defaultReleaseVersion;
    }

    public ReleaseContext setDefaultReleaseVersion(String defaultReleaseVersion)
    {
        this.defaultReleaseVersion = defaultReleaseVersion;
        return this;
    }

    public String getDefaultDevelopmentVersion()
    {
        return defaultDevelopmentVersion;
    }

    public ReleaseContext setDefaultDevelopmentVersion(String version)
    {
        this.defaultDevelopmentVersion = version;
        return this;
    }

    public String getVersionNumberToIncrement() {
		return versionNumberToIncrement;
	}

    public int getVersionNumberToIncrementAsInt() {
		return Integer.parseInt(versionNumberToIncrement);
	}

	public ReleaseContext setVersionNumberToIncrement(String versionNumberToIncrement) {
		this.versionNumberToIncrement = versionNumberToIncrement;
		return this;
	}

    public boolean isIncrementDevelopFromReleaseVersion() {
        return incrementDevelopFromReleaseVersion;
    }

    public ReleaseContext setIncrementDevelopFromReleaseVersion(boolean incrementDevelopFromReleaseVersion) {
        this.incrementDevelopFromReleaseVersion = incrementDevelopFromReleaseVersion;
        return this;
    }

    public boolean isInteractive()
    {
        return interactive;
    }

    public ReleaseContext setInteractive(boolean interactive)
    {
        this.interactive = interactive;
        return this;
    }

    public boolean isAutoVersionSubmodules()
    {
        return autoVersionSubmodules;
    }

    public ReleaseContext setAutoVersionSubmodules(boolean autoVersionSubmodules)
    {
        this.autoVersionSubmodules = autoVersionSubmodules;
        return this;
    }

    public InitContext getFlowInitContext()
    {
        return flowInitContext;
    }

    public ReleaseContext setFlowInitContext(InitContext flowInitContext)
    {
        this.flowInitContext = flowInitContext;
        return this;
    }

    public boolean isUpdateDependencies()
    {
        return updateDependencies;
    }

    public ReleaseContext setUpdateDependencies(boolean updateDependencies)
    {
        this.updateDependencies = updateDependencies;
        return this;
    }

    public File getBaseDir()
    {
        return baseDir;
    }

    public boolean isPushFeatures()
    {
        return pushFeatures;
    }

    public ReleaseContext setPushFeatures(boolean push)
    {
        this.pushFeatures = push;
        return this;
    }

    public boolean isPushReleases()
    {
        return pushReleases;
    }

    public ReleaseContext setPushReleases(boolean push)
    {
        this.pushReleases = push;
        return this;
    }

    public boolean isPushHotfixes()
    {
        return pushHotfixes;
    }

    public ReleaseContext setPushHotfixes(boolean push)
    {
        this.pushHotfixes = push;
        return this;
    }

    public boolean isKeepBranch()
    {
        return keepBranch;
    }

    public ReleaseContext setKeepBranch(boolean keepBranch)
    {
        this.keepBranch = keepBranch;
        return this;
    }

    public boolean isSquash()
    {
        return squash;
    }

    public ReleaseContext setSquash(boolean squash)
    {
        this.squash = squash;
        return this;
    }

    public boolean isNoTag()
    {
        return noTag;
    }

    public ReleaseContext setNoTag(boolean noTag)
    {
        this.noTag = noTag;
        return this;
    }

    public boolean isNoDeploy()
    {
        return noDeploy;
    }

    public ReleaseContext setNoDeploy(boolean deploy)
    {
        this.noDeploy = deploy;
        return this;
    }

    public boolean isNoBuild()
    {
        return noBuild;
    }

    /*
     * NOTE: This should only be used for testing!!!
     */
    public ReleaseContext setNoBuild(boolean nobuild)
    {
        this.noBuild = nobuild;
        return this;
    }

    public boolean isFeatureRebase()
    {
        return featureRebase;
    }

    public ReleaseContext setFeatureRebase(boolean rebase)
    {
        this.featureRebase = rebase;
        return this;
    }

    public String getTagMessage()
    {
        return tagMessage;
    }

    public ReleaseContext setTagMessage(String msg)
    {
        if (!Strings.isNullOrEmpty(msg))
        {
            this.tagMessage = msg;
        }

        return this;
    }

    public boolean isUseReleaseProfile()
    {
        return useReleaseProfile;
    }

    public ReleaseContext setUseReleaseProfile(boolean useReleaseProfile)
    {
        this.useReleaseProfile = useReleaseProfile;
        return this;
    }

    public String getArgs()
    {
        return args;
    }

    public ReleaseContext setArgs(String args)
    {
        this.args = args;
        return this;
    }

    public String getGoals() {
        return goals;
    }

    public ReleaseContext setGoals(String goals) {
        this.goals = goals;
        return this;
    }

    public String getStartCommit()
    {
        return startCommit;
    }

    public ReleaseContext setStartCommit(String commit)
    {
        this.startCommit = commit;
        return this;
    }

    public ReleaseContext setDefaultFeatureName(String defaultFeatureName)
    {
        this.defaultFeatureName = defaultFeatureName;
        return this;
    }

    public String getDefaultFeatureName()
    {
        return defaultFeatureName;
    }

    public ReleaseContext setReleaseBranchVersionSuffix(String suffix)
    {
        this.releaseBranchVersionSuffix = suffix;
        return this;
    }

    public String getReleaseBranchVersionSuffix()
    {
        return releaseBranchVersionSuffix;
    }

    public boolean isEnableFeatureVersions()
    {
        return enableFeatureVersions;
    }

    public ReleaseContext setEnableFeatureVersions(boolean enable)
    {
        this.enableFeatureVersions = enable;
        return this;
    }

    public boolean isEnableSshAgent()
    {
        return enableSshAgent;
    }

    public ReleaseContext setEnableSshAgent(boolean enableSshAgent)
    {
        this.enableSshAgent = enableSshAgent;
        return this;
    }

    public boolean isAllowUntracked()
    {
        return allowUntracked;
    }

    public ReleaseContext setAllowUntracked(boolean allow)
    {
        this.allowUntracked = allow;
        return this;
    }

    public boolean isNoReleaseMerge()
    {
        return noReleaseMerge;
    }

    public boolean isNoFeatureMerge()
    {
        return noFeatureMerge;
    }

    public ReleaseContext setNoReleaseMerge(boolean merge)
    {
        this.noReleaseMerge = merge;
        return this;
    }

    public ReleaseContext setNoFeatureMerge(boolean merge)
    {
        this.noFeatureMerge = merge;
        return this;
    }

    public boolean isSuppressFastForward()
    {
        return suppressFastForward;
    }

    public ReleaseContext setSuppressFastForward(boolean suppressFastForward)
    {
        this.suppressFastForward = suppressFastForward;
        return this;
    }

    public boolean isRemoteAllowed()
    {
        return allowRemote;
    }

    public ReleaseContext setAllowRemote(boolean allow)
    {
        this.allowRemote = allow;
        return this;
    }

    public String getDefaultOriginUrl()
    {
        return defaultOriginUrl;

    }

    public ReleaseContext setDefaultOriginUrl(String defaultOriginUrl)
    {
        this.defaultOriginUrl = defaultOriginUrl;
        return this;
    }

    public String getScmCommentPrefix()
    {
        if (null == scmCommentPrefix || scmCommentPrefix.equalsIgnoreCase("null"))
        {
            this.scmCommentPrefix = "";
        }

        return scmCommentPrefix;
    }

    public ReleaseContext setScmCommentPrefix(String scmCommentPrefix)
    {
        this.scmCommentPrefix = scmCommentPrefix;
        return this;
    }

    public String getScmCommentSuffix()
    {
        if (null == scmCommentSuffix || scmCommentSuffix.equalsIgnoreCase("null"))
        {
            this.scmCommentSuffix = "";
        }

        return scmCommentSuffix;
    }

    public ReleaseContext setScmCommentSuffix(String scmCommentSuffix)
    {
        this.scmCommentSuffix = scmCommentSuffix;
        return this;
    }

    public boolean isPullMaster()
    {
        return pullMaster;
    }

    public ReleaseContext setPullMaster(boolean pullMaster)
    {
        this.pullMaster = pullMaster;
        return this;
    }

    public ReleaseContext setPullDevelop(boolean pullDevelop)
    {
        this.pullDevelop = pullDevelop;
        return this;
    }

    public boolean isPullDevelop()
    {
        return pullDevelop;
    }

    public String getUsername()
    {
        return username;
    }

    public ReleaseContext setUsername(String username)
    {
        this.username = username;
        return this;
    }

    public String getPassword()
    {
        return password;
    }

    public ReleaseContext setPassword(String password)
    {
        this.password = password;
        return this;
    }

    public boolean isAlwaysUpdateOrigin()
    {
        return alwaysUpdateOrigin;
    }

    public ReleaseContext setAlwaysUpdateOrigin(boolean update)
    {
        this.alwaysUpdateOrigin = update;
        return this;
    }

    public boolean isConsistentProjectVersions()
    {
        return consistentProjectVersions;
    }

    public ReleaseContext setConsistentProjectVersions(boolean update)
    {
        this.consistentProjectVersions = update;
        return this;
    }

    public MavenReleaseStartExtension getReleaseStartExtension()
    {
        return releaseStartExtension;
    }

    public ReleaseContext setReleaseStartExtension(MavenReleaseStartExtension releaseStartExtension)
    {
        this.releaseStartExtension = releaseStartExtension;
        return this;
    }

    public MavenReleaseFinishExtension getReleaseFinishExtension()
    {
        return releaseFinishExtension;
    }

    public ReleaseContext setReleaseFinishExtension(MavenReleaseFinishExtension releaseFinishExtension)
    {
        this.releaseFinishExtension = releaseFinishExtension;
        return this;
    }

    public String getEol()
    {
        if (null == eol || eol.equalsIgnoreCase("null"))
        {
            this.eol = "";
        }

        return eol;
    }

    public ReleaseContext setEol(String eol)
    {
        this.eol = eol;
        return this;
    }

    public boolean isReleaseSnapshots()
    {
        return releaseSnapshots;
    }

    public ReleaseContext setReleaseSnapshots(boolean releaseSnapshots)
    {
        this.releaseSnapshots = releaseSnapshots;
        return this;
    }
    
    public MavenHotfixStartExtension getHotfixStartExtension()
    {
        return hotfixStartExtension;
    }

    public ReleaseContext setHotfixStartExtension(MavenHotfixStartExtension hotfixStartExtension)
    {
        this.hotfixStartExtension = hotfixStartExtension;
        return this;
    }

    public MavenHotfixFinishExtension getHotfixFinishExtension()
    {
        return hotfixFinishExtension;
    }

    public ReleaseContext setHotfixFinishExtension(MavenHotfixFinishExtension hotfixFinishExtension)
    {
        this.hotfixFinishExtension = hotfixFinishExtension;
        return this;
    }

    public boolean isAddScmCommentSuffixOnMerge() {
        return addScmCommentSuffixOnMerge;
    }

    public ReleaseContext setAddScmCommentSuffixOnMerge(boolean addScmCommentSuffixOnMerge) {
        this.addScmCommentSuffixOnMerge = addScmCommentSuffixOnMerge;
        return this;
    }

    public boolean isNoVerify() {
        return noVerify;
    }

    public ReleaseContext setNoVerify(boolean noVerify) {
        this.noVerify = noVerify;
        return this;
    }

    public boolean isAddFixLevel() {
        return addFixLevel;
    }

    public ReleaseContext setAddFixLevel(boolean addFixLevel) {
        this.addFixLevel = addFixLevel;
        return this;
    }
}
