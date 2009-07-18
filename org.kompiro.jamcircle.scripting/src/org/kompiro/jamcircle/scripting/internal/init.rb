require "java"
include_class "org.kompiro.jamcircle.kanban.model.mock.Card"
include_class "org.kompiro.jamcircle.kanban.model.mock.Lane"
card = $bsf.lookupBean('card');
lane = $bsf.lookupBean('lane');
event = $bsf.lookupBean('event');
board = $bsf.lookupBean('board');
monitor = $bsf.lookupBean('monitor');
JRubyType = $bsf.lookupBean('JRubyType');
JavaScriptType = $bsf.lookupBean('JavaScriptType');
RED = $bsf.lookupBean('RED');
YELLOW = $bsf.lookupBean('YELLOW');
GREEN = $bsf.lookupBean('GREEN');
LIGHT_GREEN = $bsf.lookupBean('LIGHT_GREEN');
LIGHT_BLUE = $bsf.lookupBean('LIGHT_BLUE');
BLUE = $bsf.lookupBean('BLUE');
PURPLE = $bsf.lookupBean('PURPLE');
RED_PURPLE = $bsf.lookupBean('RED_PURPLE');
FLAG_RED = $bsf.lookupBean('FLAG_RED');
FLAG_BLUE = $bsf.lookupBean('FLAG_BLUE');
FLAG_GREEN = $bsf.lookupBean('FLAG_GREEN');
FLAG_WHITE = $bsf.lookupBean('FLAG_WHITE');
FLAG_ORANGE = $bsf.lookupBean('FLAG_ORANGE');