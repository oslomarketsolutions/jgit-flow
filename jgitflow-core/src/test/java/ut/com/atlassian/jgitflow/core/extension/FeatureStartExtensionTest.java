package ut.com.atlassian.jgitflow.core.extension;

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

import com.atlassian.jgitflow.core.JGitFlow;
import com.atlassian.jgitflow.core.JGitFlowInitCommand;
import com.atlassian.jgitflow.core.exception.JGitFlowExtensionException;
import com.atlassian.jgitflow.core.extension.ExtensionFailStrategy;

import org.eclipse.jgit.api.Git;
import org.junit.Test;

import ut.com.atlassian.jgitflow.core.BaseGitFlowTest;
import ut.com.atlassian.jgitflow.core.testutils.BaseExtensionForTests;
import ut.com.atlassian.jgitflow.core.testutils.FeatureStartExtensionForTests;
import ut.com.atlassian.jgitflow.core.testutils.RepoUtil;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FeatureStartExtensionTest extends BaseGitFlowTest
{
    @Test
    public void startFeatureExtension() throws Exception
    {
        Git git = null;
        Git remoteGit = null;
        remoteGit = RepoUtil.createRepositoryWithMasterAndDevelop(newDir());

        git = Git.cloneRepository().setDirectory(newDir()).setURI("file://" + remoteGit.getRepository().getWorkTree().getPath()).call();

        JGitFlowInitCommand initCommand = new JGitFlowInitCommand();
        JGitFlow flow = initCommand.setDirectory(git.getRepository().getWorkTree()).call();
        git.push().setRemote("origin").add("develop").call();

        FeatureStartExtensionForTests extension = new FeatureStartExtensionForTests();

        flow.featureStart("myFeature").setFetch(true).setPush(true).setExtension(extension).call();

        assertTrue("before was not called", extension.wasCalled(BaseExtensionForTests.BEFORE));
        assertTrue("beforeFetch was not called", extension.wasCalled(BaseExtensionForTests.BEFORE_FETCH));
        assertTrue("afterFetch was not called", extension.wasCalled(BaseExtensionForTests.AFTER_FETCH));
        assertTrue("beforeCreateBranch was not called", extension.wasCalled(BaseExtensionForTests.BEFORE_CREATE_BRANCH));
        assertTrue("afterCreateBranch was not called", extension.wasCalled(BaseExtensionForTests.AFTER_CREATE_BRANCH));
        assertTrue("afterPush was not called", extension.wasCalled(BaseExtensionForTests.AFTER_PUSH));
        assertTrue("after was not called", extension.wasCalled(BaseExtensionForTests.AFTER));
    }

    @Test
    public void startFeatureExtensionWithThrownException() throws Exception
    {
        Git git = null;
        Git remoteGit = null;
        remoteGit = RepoUtil.createRepositoryWithMasterAndDevelop(newDir());

        git = Git.cloneRepository().setDirectory(newDir()).setURI("file://" + remoteGit.getRepository().getWorkTree().getPath()).call();

        JGitFlowInitCommand initCommand = new JGitFlowInitCommand();
        JGitFlow flow = initCommand.setDirectory(git.getRepository().getWorkTree()).call();
        git.push().setRemote("origin").add("develop").call();

        FeatureStartExtensionForTests extension = new FeatureStartExtensionForTests();
        extension.withException(BaseExtensionForTests.AFTER_CREATE_BRANCH, ExtensionFailStrategy.ERROR);

        try
        {
            flow.featureStart("myFeature").setFetch(true).setPush(true).setExtension(extension).call();

            fail("Exception should have been thrown!!");
        }
        catch (JGitFlowExtensionException e)
        {
            assertTrue("before was not called", extension.wasCalled(BaseExtensionForTests.BEFORE));
            assertTrue("beforeFetch was not called", extension.wasCalled(BaseExtensionForTests.BEFORE_FETCH));
            assertTrue("afterFetch was not called", extension.wasCalled(BaseExtensionForTests.AFTER_FETCH));
            assertTrue("beforeCreateBranch was not called", extension.wasCalled(BaseExtensionForTests.BEFORE_CREATE_BRANCH));
        }

    }

    @Test
    public void startFeatureExtensionWithWarnException() throws Exception
    {
        Git git = null;
        Git remoteGit = null;
        remoteGit = RepoUtil.createRepositoryWithMasterAndDevelop(newDir());

        git = Git.cloneRepository().setDirectory(newDir()).setURI("file://" + remoteGit.getRepository().getWorkTree().getPath()).call();

        JGitFlowInitCommand initCommand = new JGitFlowInitCommand();
        JGitFlow flow = initCommand.setDirectory(git.getRepository().getWorkTree()).call();
        git.push().setRemote("origin").add("develop").call();

        FeatureStartExtensionForTests extension = new FeatureStartExtensionForTests();
        extension.withException(BaseExtensionForTests.AFTER_CREATE_BRANCH, ExtensionFailStrategy.WARN);

        flow.featureStart("myFeature").setFetch(true).setPush(true).setExtension(extension).call();

        assertTrue("before was not called", extension.wasCalled(BaseExtensionForTests.BEFORE));
        assertTrue("beforeFetch was not called", extension.wasCalled(BaseExtensionForTests.BEFORE_FETCH));
        assertTrue("afterFetch was not called", extension.wasCalled(BaseExtensionForTests.AFTER_FETCH));
        assertTrue("beforeCreateBranch was not called", extension.wasCalled(BaseExtensionForTests.BEFORE_CREATE_BRANCH));
        assertTrue("afterCreateBranch was not called", extension.wasCalled(BaseExtensionForTests.AFTER_CREATE_BRANCH));
        assertTrue("afterPush was not called", extension.wasCalled(BaseExtensionForTests.AFTER_PUSH));
        assertTrue("after was not called", extension.wasCalled(BaseExtensionForTests.AFTER));

    }

}
