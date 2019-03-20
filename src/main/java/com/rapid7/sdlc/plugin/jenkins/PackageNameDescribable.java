package com.rapid7.sdlc.plugin.jenkins;

import com.rapid7.sdlc.plugin.ruleset.action.Action;
import com.rapid7.sdlc.plugin.ruleset.action.FailAction;
import com.rapid7.sdlc.plugin.ruleset.action.MarkUnstableAction;
import com.rapid7.sdlc.plugin.ruleset.property.PackageName;
import com.rapid7.sdlc.plugin.ruleset.property.PropertyEvaluator;
import hudson.Extension;
import hudson.Util;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import javax.annotation.Nonnull;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

public class PackageNameDescribable extends NameRuleDescribable {
  private final String action;
  private final String contains;
  private final PackageName rule;

  @DataBoundConstructor
  public PackageNameDescribable(String action, String contains) {
    this.action = action;
    this.contains = contains;
    this.rule = new PackageName(contains);
  }

  public String getAction() {
    return action;
  }

  public String getContains() {
    return contains;
  }

  @Override
  public PropertyEvaluator getPropertyEvaluator() {
    return rule;
  }

  @Override
  public Action getActionObject() {
    if ("Mark Unstable".equalsIgnoreCase(action))
      return new MarkUnstableAction();
    else if ("Fail".equalsIgnoreCase(action))
      return new FailAction();
    else
      return null;
  }

  @Extension
  @Symbol("packageName")
  public static class DescriptorImpl extends Descriptor<NameRuleDescribable> {
    @Nonnull
    @Override
    public String getDisplayName() {
      return Messages.ContainerAssessmentBuilder_PackageNameRule();
    }

    public ListBoxModel doFillActionItems() {
      return new ListBoxModel().add("Fail").add("Mark Unstable");
    }

    public FormValidation doCheckContains(@QueryParameter String value, @AncestorInPath Item item) {
      if (item == null) {
        return FormValidation.ok();
      }
      item.checkPermission(Item.CONFIGURE);

      if (Util.fixEmptyAndTrim(value) == null)
        return FormValidation.error(Messages.ContainerAssessmentBuilder_PatternRuleValidationEmpty());

      return FormValidation.ok();
    }
  }
}
