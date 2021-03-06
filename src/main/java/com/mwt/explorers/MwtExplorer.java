package com.mwt.explorers;

import com.mwt.misc.DecisionTuple;
import com.mwt.recorders.Recorder;
import com.mwt.utilities.MurMurHash3;

/**
 * The top-level MwtExplorer class. Using this enables principled and efficient exploration
 * over a set of possible actions, and ensures that the right bits are recorded.
 */
public class MwtExplorer<T> {
  private long appId;
  private Recorder<T> recorder;

  /**
   * MwtExplorer constructor
   * @param appId
   * @param recorder
   */
  public MwtExplorer(String appId, Recorder<T> recorder) {
    this.appId = MurMurHash3.computeIdHash(appId);
    this.recorder = recorder;
  }

  /**
   * Chooses an action by invoking an underlying exploration algorithm. This should be a
   * drop-in replacement for any existing policy function.   
   *
   * @param explorer    An existing exploration algorithm (one of the below) which uses the default policy as a callback.
   * @param uniqueKey  A unique identifier for the experimental unit. This could be a user id, a session id, etc..
   * @param context     The context upon which a decision is made. See SimpleContext below for an example.
   *
   * @return The chosen action
   */
  public int chooseAction(Explorer<T> explorer, String uniqueKey, T context) {
    long seed = MurMurHash3.computeIdHash(uniqueKey);
    DecisionTuple decisionTuple = explorer.chooseAction(seed + appId, context);

    if (decisionTuple.shouldRecord()) {
      recorder.record(context, decisionTuple.getAction(), decisionTuple.getProbability(), uniqueKey);
    }

    return decisionTuple.getAction();
  }
}
