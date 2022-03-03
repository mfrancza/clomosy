# Clomosy
A Clojure-based modular synthesizer

## How To Run

The main entry point for clomosy takes a single command line argument containing a clojure map with the :frame-rate and :synth-def keys.  

:frame-rate is the number of update frames per second the synthesizer uses and will generally match the sample rates of the audio inputs and outputs of the synthesizer.  

:synth-def should be a clojure function which will take the frame rate as its only argument and return a synthesizer definition.

## Design

Clomosy attempts to approximate the abstractions presented by physical modular synthesizers by defining modules which communicate with each other in terms of floating-point values in the same way voltages are used to communicate between modules in analog synthesizers.  Rather than trying to define types of signals, Clomosy encourages patching modules together in unexpected ways to make interesting sounds.

### Main Loop

The main loop of the synthesizer consists of determining the next state of the synthesizer by evaluating the modules in order of their input dependencies, passing the current state of the module and outputs from other modules - each iteration of this process is called a frame.  Each module returns its updated state and output values which are used as the inputs for subsequent modules evaluated in the same frame.  Module state is used for modules to communicate with themselves across frames and inputs and outputs are used for communcating between modules within a frame.

The number of iterations per second of this loop is the frame-rate of the synthesizer and is analogous to (and should generally be the same value as) the sample rate for audio inputs and outputs.

### Synthesizer Definitions

A synthesizer is defined by creating module definitions and patches mapping inputs to outputs.  The manditory function defining a module is the update-fn, which takes the inputs and current state of the module and returns its outputs.  Update-fns may cause external side effects, most notably audio output to devices, but should not have side effects impacting other modules' state.