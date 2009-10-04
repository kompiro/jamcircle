require "java"
include_class "org.kompiro.jamcircle.kanban.model.mock.Card"
include_class "org.kompiro.jamcircle.kanban.model.mock.Lane"
card = $bsf.lookupBean('card');
lane = $bsf.lookupBean('lane');
event = $bsf.lookupBean('event');
board = $bsf.lookupBean('board');
monitor = $bsf.lookupBean('monitor');
