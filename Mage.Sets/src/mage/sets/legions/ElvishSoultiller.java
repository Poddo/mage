/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.sets.legions;

import java.util.UUID;
import mage.MageInt;
import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.common.DiesTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.repository.CardRepository;
import mage.choices.Choice;
import mage.choices.ChoiceImpl;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.util.CardUtil;

/**
 *
 * @author anonymous
 */
public class ElvishSoultiller extends CardImpl {

    public ElvishSoultiller(UUID ownerId) {
        super(ownerId, 124, "Elvish Soultiller", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{3}{G}{G}");
        this.expansionSetCode = "LGN";
        this.subtype.add("Elf");
        this.subtype.add("Mutant");
        this.power = new MageInt(5);
        this.toughness = new MageInt(4);

        // When Elvish Soultiller dies, choose a creature type. Shuffle all creature cards of that type from your graveyard into your library.
        this.addAbility(new DiesTriggeredAbility(new ElvishSoulltillerEffect()));
    }

    public ElvishSoultiller(final ElvishSoultiller card) {
        super(card);
    }

    @Override
    public ElvishSoultiller copy() {
        return new ElvishSoultiller(this);
    }
}

class ElvishSoulltillerEffect extends OneShotEffect {
    
    public ElvishSoulltillerEffect() {
        super(Outcome.Benefit);
        staticText = "EFFECT TEXT";
    }

    public ElvishSoulltillerEffect(final ElvishSoulltillerEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
        MageObject mageObject = game.getPermanentEntering(source.getSourceId());
        if (mageObject == null) {
            mageObject = game.getObject(source.getSourceId());
        }
        if (controller != null && mageObject != null) {
            Choice typeChoice = new ChoiceImpl(true);
            typeChoice.setMessage("Choose creature type");
            typeChoice.setChoices(CardRepository.instance.getCreatureTypes());
            while (!controller.choose(outcome, typeChoice, game)) {
                if (!controller.canRespond()) {
                    return false;
                }
            }
            if (!game.isSimulation()) {
                game.informPlayers(mageObject.getName() + ": " + controller.getLogName() + " has chosen " + typeChoice.getChoice());
            }
            game.getState().setValue(mageObject.getId() + "_type", typeChoice.getChoice());
            if (mageObject instanceof Permanent) {
                ((Permanent) mageObject).addInfo("chosen type", CardUtil.addToolTipMarkTags("Chosen type: " + typeChoice.getChoice()), game);
            }
            
            String typeName = typeChoice.getChoice();
            CardType chosenType = null;
            for (CardType cardType : CardType.values()) {
                if (cardType.toString().equals(typeName)) {
                    chosenType = cardType;
                }
            }
            if (chosenType != null) {
                for (Card card : controller.getGraveyard().getCards(game)) {
                    if (card.getCardType().contains(chosenType)) {
                        card.moveToZone(Zone.LIBRARY, source.getSourceId(), game, false);
                    }
                }
                controller.shuffleLibrary(game);
                return true;
            }
        }        
        return false;
    }

    @Override
    public ElvishSoulltillerEffect copy() {
        return new ElvishSoulltillerEffect(this);
    }

}