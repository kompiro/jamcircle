require "java"
Card = org.kompiro.jamcircle.kanban.model.mock.Card
Lane = org.kompiro.jamcircle.kanban.model.mock.Lane
User = org.kompiro.jamcircle.kanban.model.mock.User
card = $bsf.lookupBean('card');
lane = $bsf.lookupBean('lane');
event = $bsf.lookupBean('event');
board = $bsf.lookupBean('board');
monitor = $bsf.lookupBean('monitor');
